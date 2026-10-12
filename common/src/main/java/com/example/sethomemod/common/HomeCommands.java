package com.example.sethomemod.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HomeCommands {
    private final HomeManager homeManager;
    private static final File LANGUAGE_FILE = new File("config/tpsethome/language.txt");

    public HomeCommands(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    public void sethome(GamePlayer player, String homeName) {
        homeManager.setHome(
            player.getUuid(),
            homeName,
            player.getX(), player.getY(), player.getZ(),
            player.getDimensionId()
        );
        player.sendMessage(message("Home '" + homeName + "' set successfully!", "Home '" + homeName + "' défini avec succès !"));
    }

    public void home(GamePlayer player, String homeName) {
        HomeData home = homeManager.getHome(player.getUuid(), homeName);
        if (home == null) {
            player.sendMessage(message("Home '" + homeName + "' does not exist.", "Le home '" + homeName + "' n'existe pas."));
            return;
        }
        player.teleport(home.x, home.y, home.z, home.dimensionId);
        player.sendMessage(message("Teleported to '" + homeName + "'!", "Téléporté à '" + homeName + "' !"));
    }

    public void delhome(GamePlayer player, String homeName) {
        boolean removed = homeManager.delHome(player.getUuid(), homeName);
        if (removed) {
            player.sendMessage(message("Home '" + homeName + "' deleted.", "Home '" + homeName + "' supprimé."));
        } else {
            player.sendMessage(message("Home '" + homeName + "' does not exist.", "Le home '" + homeName + "' n'existe pas."));
        }
    }

    private static String message(String english, String french) {
        return isFrench() ? french : english;
    }

    private static boolean isFrench() {
        BufferedReader reader = null;
        try {
            File parent = LANGUAGE_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!LANGUAGE_FILE.exists()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LANGUAGE_FILE), "UTF-8"));
                try {
                    writer.write("en");
                    writer.newLine();
                } finally {
                    writer.close();
                }
            }
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(LANGUAGE_FILE), "UTF-8"));
            String language = reader.readLine();
            return language != null && (language.trim().equalsIgnoreCase("fr") || language.trim().equalsIgnoreCase("français") || language.trim().equalsIgnoreCase("french"));
        } catch (Exception ignored) {
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
