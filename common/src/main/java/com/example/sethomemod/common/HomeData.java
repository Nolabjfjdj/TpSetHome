package com.example.sethomemod.common;

public class HomeData {
    public final String name;
    public final double x;
    public final double y;
    public final double z;
    public final String dimensionId; // ex: "minecraft:overworld", "minecraft:the_nether"

    public HomeData(String name, double x, double y, double z, String dimensionId) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensionId = dimensionId;
    }
}
