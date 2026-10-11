package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.UUID;

public final class ForgeGamePlayer implements GamePlayer {
    private final EntityPlayerMP player;

    public ForgeGamePlayer(EntityPlayerMP player) {
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
        return Integer.toString(player.dimension);
    }

    @Override
    public void teleport(double x, double y, double z, String dimensionId) {
        if (getDimensionId().equals(dimensionId)) {
            player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        } else {
            sendMessage("Ce home se trouve dans une autre dimension, la téléportation n'est pas prise en charge sur cette version.");
        }
    }

    @Override
    public void sendMessage(String message) {
        player.addChatMessage(new TextComponentString(message));
    }
}
