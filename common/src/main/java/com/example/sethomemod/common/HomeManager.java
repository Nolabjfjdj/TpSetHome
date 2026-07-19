package com.example.sethomemod.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    private final HomeStorage storage;
    private final Map<UUID, Map<String, HomeData>> cache = new ConcurrentHashMap<>();

    public HomeManager(HomeStorage storage) {
        this.storage = storage;
    }

    private Map<String, HomeData> homesOf(UUID playerId) {
        return cache.computeIfAbsent(playerId, storage::load);
    }

    public void setHome(UUID playerId, String name, double x, double y, double z, String dimensionId) {
        Map<String, HomeData> homes = homesOf(playerId);
        homes.put(name, new HomeData(name, x, y, z, dimensionId));
        storage.save(playerId, homes);
    }

    public HomeData getHome(UUID playerId, String name) {
        return homesOf(playerId).get(name);
    }

    public boolean delHome(UUID playerId, String name) {
        Map<String, HomeData> homes = homesOf(playerId);
        boolean removed = homes.remove(name) != null;
        if (removed) {
            storage.save(playerId, homes);
        }
        return removed;
    }

    public Map<String, HomeData> listHomes(UUID playerId) {
        return new HashMap<>(homesOf(playerId));
    }
}
