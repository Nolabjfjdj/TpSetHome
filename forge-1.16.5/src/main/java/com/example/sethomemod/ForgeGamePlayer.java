package com.example.sethomemod;

import com.example.sethomemod.common.GamePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
        return player.getPosX();
    }

    @Override
    public double getY() {
        return player.getPosY();
    }

    @Override
    public double getZ() {
        return player.getPosZ();
    }

    @Override
    public String getDimensionId() {
        return player.getEntityWorld().getDimensionKey().getLocation().toString();
    }

    @Override
    public void teleport(double x, double y, double z, String dimensionId) {
        RegistryKey<World> key = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimensionId));
        ServerWorld targetLevel = player.getServer().getWorld(key);
        if (targetLevel != null) {
            player.teleport(targetLevel, x, y, z, player.rotationYaw, player.rotationPitch);
        } else {
            sendMessage("Impossible de trouver la dimension du home.");
        }
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(new StringTextComponent(message), player.getUniqueID());
    }
}
