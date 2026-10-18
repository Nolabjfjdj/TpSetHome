import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.awt.event.KeyEvent;

public final class TpSetHomeAlpha {
    private TpSetHomeAlpha() {
    }

    public static boolean handle(Object screen, Object event) {
        try {
            if (!(event instanceof KeyEvent) || ((KeyEvent) event).getKeyCode() != KeyEvent.VK_ENTER) {
                return false;
            }
            String input = findCommandText(screen);
            if (input == null) {
                return false;
            }
            String command = input.trim();
            if (command.equalsIgnoreCase("/sethome")) {
                Object minecraft = findMinecraft(screen);
                Object player = findPlayer(minecraft);
                if (player == null) {
                    showMessage(minecraft, "TpSetHome: joueur introuvable.");
                    return true;
                }
                double[] position = readPosition(player);
                if (position == null) {
                    showMessage(minecraft, "TpSetHome: position du joueur introuvable.");
                    return true;
                }
                File file = homeFile(minecraft);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                try {
                    writer.write(Double.toString(position[0]));
                    writer.write(",");
                    writer.write(Double.toString(position[1]));
                    writer.write(",");
                    writer.write(Double.toString(position[2]));
                    writer.newLine();
                } finally {
                    writer.close();
                }
                showMessage(minecraft, "TpSetHome: maison sauvegardee.");
                return true;
            }
            if (command.equalsIgnoreCase("/home")) {
                Object minecraft = findMinecraft(screen);
                Object player = findPlayer(minecraft);
                File file = homeFile(minecraft);
                if (!file.isFile()) {
                    showMessage(minecraft, "TpSetHome: aucune maison. Utilise /sethome.");
                    return true;
                }
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                try {
                    line = reader.readLine();
                } finally {
                    reader.close();
                }
                if (line == null) {
                    showMessage(minecraft, "TpSetHome: sauvegarde invalide.");
                    return true;
                }
                String[] values = line.split(",");
                if (values.length != 3) {
                    showMessage(minecraft, "TpSetHome: sauvegarde invalide.");
                    return true;
                }
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                double z = Double.parseDouble(values[2]);
                if (player == null || !teleport(player, x, y, z)) {
                    showMessage(minecraft, "TpSetHome: teletransportation impossible.");
                    return true;
                }
                showMessage(minecraft, "TpSetHome: retour a la maison.");
                return true;
            }
            if (command.equalsIgnoreCase("/delhome")) {
                Object minecraft = findMinecraft(screen);
                File file = homeFile(minecraft);
                if (file.exists() && !file.delete()) {
                    showMessage(minecraft, "TpSetHome: impossible de supprimer la maison.");
                } else {
                    showMessage(minecraft, "TpSetHome: maison supprimee.");
                }
                return true;
            }
            return false;
        } catch (Throwable error) {
            error.printStackTrace();
            return false;
        }
    }

    private static String findCommandText(Object screen) {
        String result = null;
        Class<?> type = screen.getClass();
        while (type != null) {
            Field[] fields = type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getType() == String.class && !Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(screen);
                        if (value instanceof String) {
                            String text = ((String) value).trim();
                            if (text.startsWith("/") && (result == null || text.length() > result.length())) {
                                result = text;
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
            type = type.getSuperclass();
        }
        return result;
    }

    private static Object findMinecraft(Object screen) {
        Class<?> type = screen.getClass();
        while (type != null) {
            Field[] fields = type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (!Modifier.isStatic(field.getModifiers()) && field.getType().getName().equals("net.minecraft.client.Minecraft")) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(screen);
                        if (value != null) {
                            return value;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private static Object findPlayer(Object minecraft) {
        if (minecraft == null) {
            return null;
        }
        Object player = getNamedField(minecraft, "p");
        if (player != null) {
            return player;
        }
        Class<?> type = minecraft.getClass();
        while (type != null) {
            Field[] fields = type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (Modifier.isStatic(field.getModifiers()) || field.getType().isPrimitive()) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object value = field.get(minecraft);
                    if (value != null && findCoordinateFields(value) != null) {
                        return value;
                    }
                } catch (Throwable ignored) {
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private static Object getNamedField(Object target, String name) {
        if (target == null) {
            return null;
        }
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (Throwable ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }

    private static Field[] findCoordinateFields(Object player) {
        Class<?> type = player.getClass();
        while (type != null) {
            try {
                Field x = type.getDeclaredField("ae");
                Field y = type.getDeclaredField("af");
                Field z = type.getDeclaredField("ag");
                if (x.getType() == Double.TYPE && y.getType() == Double.TYPE && z.getType() == Double.TYPE) {
                    x.setAccessible(true);
                    y.setAccessible(true);
                    z.setAccessible(true);
                    return new Field[] { x, y, z };
                }
            } catch (Throwable ignored) {
            }
            Field[] fields = type.getDeclaredFields();
            Field[] doubles = new Field[3];
            int count = 0;
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType() == Double.TYPE && !Modifier.isStatic(fields[i].getModifiers())) {
                    doubles[count++] = fields[i];
                    if (count == 3) {
                        for (int j = 0; j < 3; j++) {
                            doubles[j].setAccessible(true);
                        }
                        return doubles;
                    }
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private static double[] readPosition(Object player) throws Exception {
        Field[] fields = findCoordinateFields(player);
        if (fields == null) {
            return null;
        }
        return new double[] { fields[0].getDouble(player), fields[1].getDouble(player), fields[2].getDouble(player) };
    }

    private static boolean teleport(Object player, double x, double y, double z) {
        try {
            Class<?> type = player.getClass();
            while (type != null) {
                Method[] methods = type.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    Class<?>[] parameters = methods[i].getParameterTypes();
                    if (methods[i].getReturnType() == Void.TYPE && parameters.length == 3 &&
                        parameters[0] == Double.TYPE && parameters[1] == Double.TYPE && parameters[2] == Double.TYPE) {
                        methods[i].setAccessible(true);
                        methods[i].invoke(player, new Object[] { Double.valueOf(x), Double.valueOf(y), Double.valueOf(z) });
                        Field[] coordinates = findCoordinateFields(player);
                        if (coordinates != null) {
                            coordinates[0].setDouble(player, x);
                            coordinates[1].setDouble(player, y);
                            coordinates[2].setDouble(player, z);
                        }
                        setOptionalDouble(player, "ah", x);
                        setOptionalDouble(player, "ai", y);
                        setOptionalDouble(player, "aj", z);
                        return true;
                    }
                }
                type = type.getSuperclass();
            }
            Field[] coordinates = findCoordinateFields(player);
            if (coordinates == null) {
                return false;
            }
            coordinates[0].setDouble(player, x);
            coordinates[1].setDouble(player, y);
            coordinates[2].setDouble(player, z);
            setOptionalDouble(player, "ah", x);
            setOptionalDouble(player, "ai", y);
            setOptionalDouble(player, "aj", z);
            return true;
        } catch (Throwable error) {
            error.printStackTrace();
            return false;
        }
    }

    private static void setOptionalDouble(Object target, String name, double value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                if (field.getType() == Double.TYPE) {
                    field.setAccessible(true);
                    field.setDouble(target, value);
                }
                return;
            } catch (Throwable ignored) {
                type = type.getSuperclass();
            }
        }
    }

    private static File homeFile(Object minecraft) {
        Object directory = getNamedField(minecraft, "C");
        if (directory instanceof File) {
            return new File((File) directory, "tpsethome-alpha-a1.0.16.txt");
        }
        return new File(new File(System.getProperty("user.home"), ".minecraft"), "tpsethome-alpha-a1.0.16.txt");
    }

    private static void showMessage(Object minecraft, String message) {
        try {
            Object hud = getNamedField(minecraft, "u");
            if (hud == null) {
                System.out.println(message);
                return;
            }
            Class<?> type = hud.getClass();
            while (type != null) {
                try {
                    Method method = type.getDeclaredMethod("a", new Class[] { String.class });
                    if (method.getReturnType() == Void.TYPE) {
                        method.setAccessible(true);
                        method.invoke(hud, new Object[] { message });
                        return;
                    }
                } catch (Throwable ignored) {
                }
                type = type.getSuperclass();
            }
            System.out.println(message);
        } catch (Throwable ignored) {
            System.out.println(message);
        }
    }
}
