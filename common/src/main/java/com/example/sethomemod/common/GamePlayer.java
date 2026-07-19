package com.example.sethomemod.common;

import java.util.UUID;

public interface GamePlayer {
    UUID getUuid();
    double getX();
    double getY();
    double getZ();
    String getDimensionId();
    void teleport(double x, double y, double z, String dimensionId);
    void sendMessage(String message);
}
