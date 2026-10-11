package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;

public class FabricGamePlayer implements GamePlayer {
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
        ServerLevel targetLevel = player.level().getServer().getLevel(
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimensionId))
        );
        if (targetLevel != null) {
            player.teleportTo(
                targetLevel,
                x, y, z,
                Set.of(),
                player.getYRot(),
                player.getXRot(),
                false
            );
        }
    }

    @Override
    public void sendMessage(String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}
