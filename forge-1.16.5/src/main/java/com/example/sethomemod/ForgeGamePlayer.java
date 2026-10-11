package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.StringTextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.UUID;

public final class ForgeGamePlayer implements GamePlayer {
    private final ServerPlayer player;

    public ForgeGamePlayer(ServerPlayer player) {
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
        return player.getLevel().dimension().location().toString();
    }

    @Override
    public void teleport(double x, double y, double z, String dimensionId) {
        ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionId));
        ServerLevel targetLevel = player.getServer().getLevel(key);
        if (targetLevel != null) {
            player.teleportTo(targetLevel, x, y, z, player.getYRot(), player.getXRot());
        } else {
            sendMessage("Impossible de trouver la dimension du home.");
        }
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(new StringTextComponent(message), player.getUUID());
    }
}
