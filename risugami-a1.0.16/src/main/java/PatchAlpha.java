import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class PatchAlpha {
    private static final String SCREEN_BASE = "bb";
    private static final String KEY_METHOD_DESCRIPTOR = "(CI)V";

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: PatchAlpha <client.jar> <helper.class> <output.zip>");
        }
        JarFile jar = new JarFile(args[0]);
        Map<String, String> superNames = new HashMap<String, String>();
        List<String> classEntries = new ArrayList<String>();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                classEntries.add(entry.getName());
                InputStream input = jar.getInputStream(entry);
                try {
                    final String[] name = new String[1];
                    final String[] parent = new String[1];
                    new ClassReader(input).accept(new ClassVisitor(Opcodes.ASM5) {
                        public void visit(int version, int access, String className, String signature, String superName, String[] interfaces) {
                            name[0] = className;
                            parent[0] = superName;
                        }
                    }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    if (name[0] != null) {
                        superNames.put(name[0], parent[0]);
                    }
                } finally {
                    input.close();
                }
            }
        }

        Set<String> targets = new HashSet<String>();
        for (String entryName : classEntries) {
            String internalName = entryName.substring(0, entryName.length() - 6);
            if (!isScreenClass(internalName, superNames)) {
                continue;
            }
            InputStream input = jar.getInputStream(jar.getJarEntry(entryName));
            final boolean[] found = new boolean[] { false };
            try {
                new ClassReader(input).accept(new ClassVisitor(Opcodes.ASM5) {
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        if ((access & Opcodes.ACC_STATIC) == 0 && KEY_METHOD_DESCRIPTOR.equals(desc)) {
                            found[0] = true;
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } finally {
                input.close();
            }
            if (found[0]) {
                targets.add(entryName);
            }
        }

        if (targets.isEmpty()) {
            jar.close();
            throw new IllegalStateException("No instance (char,int) input methods were found in GUI screens descending from bb.");
        }

        File output = new File(args[2]);
        File parent = output.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(output));
        try {
            for (String entryName : targets) {
                JarEntry original = jar.getJarEntry(entryName);
                InputStream input = jar.getInputStream(original);
                byte[] originalBytes;
                try {
                    originalBytes = readAll(input);
                } finally {
                    input.close();
                }
                ClassReader reader = new ClassReader(originalBytes);
                final int[] patched = new int[] { 0 };
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        MethodVisitor base = super.visitMethod(access, name, desc, signature, exceptions);
                        if ((access & Opcodes.ACC_STATIC) == 0 && KEY_METHOD_DESCRIPTOR.equals(desc)) {
                            patched[0]++;
                            return new MethodVisitor(Opcodes.ASM5, base) {
                                public void visitCode() {
                                    super.visitCode();
                                    Label continueOriginal = new Label();
                                    super.visitVarInsn(Opcodes.ALOAD, 0);
                                    super.visitVarInsn(Opcodes.ILOAD, 1);
                                    super.visitVarInsn(Opcodes.ILOAD, 2);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "TpSetHomeAlpha", "handle", "(Ljava/lang/Object;CI)Z", false);
                                    super.visitJumpInsn(Opcodes.IFEQ, continueOriginal);
                                    super.visitInsn(Opcodes.RETURN);
                                    super.visitLabel(continueOriginal);
                                }
                            };
                        }
                        return base;
                    }
                };
                reader.accept(visitor, 0);
                if (patched[0] == 0) {
                    throw new IllegalStateException("Failed to patch " + entryName);
                }
                zip.putNextEntry(new ZipEntry(entryName));
                zip.write(writer.toByteArray());
                zip.closeEntry();
                System.out.println("Patched screen input class: " + entryName);
            }
            zip.putNextEntry(new ZipEntry("TpSetHomeAlpha.class"));
            FileInputStream helper = new FileInputStream(args[1]);
            try {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = helper.read(buffer)) != -1) {
                    zip.write(buffer, 0, read);
                }
            } finally {
                helper.close();
            }
            zip.closeEntry();
        } finally {
            zip.close();
            jar.close();
        }
        System.out.println("Created Alpha patch ZIP with " + targets.size() + " screen input class(es).");
    }

    private static boolean isScreenClass(String name, Map<String, String> superNames) {
        String current = name;
        Set<String> visited = new HashSet<String>();
        while (current != null && visited.add(current)) {
            if (SCREEN_BASE.equals(current)) {
                return true;
            }
            current = superNames.get(current);
        }
        return false;
    }

    private static byte[] readAll(InputStream input) throws Exception {
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }
}
