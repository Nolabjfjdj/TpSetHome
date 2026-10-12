package com.example.sethomemod.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HomeStorage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path baseDir;

    public HomeStorage(Path baseDir) {
        this.baseDir = baseDir;
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the TpSetHome storage directory", e);
        }
    }

    public String getLanguage() {
        Path languageFile = baseDir.resolve("language.txt");
        if (!Files.exists(languageFile)) {
            return "en";
        }
        try {
            String language = new String(Files.readAllBytes(languageFile), StandardCharsets.UTF_8)
                .trim().toLowerCase(Locale.ROOT);
            return "fr".equals(language) ? "fr" : "en";
        } catch (IOException e) {
            return "en";
        }
    }

    private Path fileFor(UUID playerId) {
        return baseDir.resolve(playerId.toString() + ".json");
    }

    public Map<String, HomeData> load(UUID playerId) {
        Path file = fileFor(playerId);
        if (!Files.exists(file)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, HomeData>>() {}.getType();
            Map<String, HomeData> result = gson.fromJson(reader, type);
            if (result == null) {
                return new HashMap<String, HomeData>();
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Could not read homes for player " + playerId, e);
        }
    }

    public void save(UUID playerId, Map<String, HomeData> homes) {
        Path file = fileFor(playerId);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(homes, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save homes for player " + playerId, e);
        }
    }
}
