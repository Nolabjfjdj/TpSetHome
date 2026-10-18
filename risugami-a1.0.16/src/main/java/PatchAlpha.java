import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
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
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: PatchAlpha <client.jar> <helper.class> <output.zip>");
        }
        JarFile jar = new JarFile(args[0]);
        JarEntry target = jar.getJarEntry("ab.class");
        if (target == null) {
            throw new IllegalStateException("The expected Alpha a1.0.16 chat class ab.class was not found.");
        }
        InputStream input = jar.getInputStream(target);
        ClassReader reader;
        try {
            reader = new ClassReader(input);
        } finally {
            input.close();
        }
        final int[] patched = new int[] { 0 };
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor base = super.visitMethod(access, name, desc, signature, exceptions);
                if ("keyTyped".equals(name) && "(Ljava/awt/event/KeyEvent;)V".equals(desc)) {
                    patched[0]++;
                    return new MethodVisitor(Opcodes.ASM5, base) {
                        public void visitCode() {
                            super.visitCode();
                            Label continueOriginal = new Label();
                            super.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "TpSetHomeAlpha", "handle", "(Ljava/lang/Object;Ljava/lang/Object;)Z", false);
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
        if (patched[0] != 1) {
            throw new IllegalStateException("Expected exactly one keyTyped(KeyEvent) method in ab.class, found " + patched[0]);
        }
        File output = new File(args[2]);
        File parent = output.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(output));
        try {
            zip.putNextEntry(new ZipEntry("ab.class"));
            zip.write(writer.toByteArray());
            zip.closeEntry();
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
        System.out.println("Created patch ZIP with ab.class and TpSetHomeAlpha.class.");
    }
}
