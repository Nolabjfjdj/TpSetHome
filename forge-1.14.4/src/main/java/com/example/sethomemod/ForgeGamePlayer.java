package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public final class ForgeGamePlayer implements GamePlayer {
    private final ServerPlayerEntity player;

    public ForgeGamePlayer(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public UUID getUuid() {
        return player.getUniqueID();
    }

    @Override
    public double getX() {
        return player.posX;
    }

    @Override
    public double getY() {
        return player.posY;
    }

    @Override
    public double getZ() {
        return player.posZ;
    }

    @Override
    public String getDimensionId() {
        ResourceLocation dimensionId = player.world.getDimension().getType().getRegistryName();
        return dimensionId == null ? "minecraft:overworld" : dimensionId.toString();
    }

    @Override
    public void teleport(double x, double y, double z, String dimensionId) {
        if (getDimensionId().equals(dimensionId)) {
            player.setPositionAndUpdate(x, y, z);
        } else {
            sendMessage("Ce home se trouve dans une autre dimension, la téléportation n'est pas prise en charge sur cette version.");
        }
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(new StringTextComponent(message));
    }
}
