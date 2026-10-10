package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public final class FabricGamePlayer implements GamePlayer {
    private final ServerPlayer player;

    public FabricGamePlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public UUID getUuid() {
        return player.getUUID();
    }

    @Override
    public double getX() {
        return player.getX();
    }

    @Override
    public double getY() {
        return player.getY();
    }

    @Override
    public double getZ() {
        return player.getZ();
    }

    @Override
    public String getDimensionId() {
        return player.level().dimension().location().toString();
    }

    @Override
    public void teleport(double x, double y, double z, String dimensionId) {
        ResourceLocation location = new ResourceLocation(dimensionId);
        ResourceKey<net.minecraft.world.level.Level> key = ResourceKey.create(Registries.DIMENSION, location);
        ServerLevel targetLevel = player.getServer().getLevel(key);
        if (targetLevel != null) {
            player.teleportTo(targetLevel, x, y, z, player.getYRot(), player.getXRot());
        } else {
            sendMessage("Impossible de trouver la dimension du home.");
        }
    }

    @Override
    public void sendMessage(String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}
