package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public final class SetHomeModFabric implements ModInitializer {
    private HomeCommands homeCommands;

    @Override
    public void onInitialize() {
        HomeStorage storage = new HomeStorage(FabricLoader.getInstance().getConfigDir().resolve("tpsethome"));
        homeCommands = new HomeCommands(new HomeManager(storage));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("sethome")
                .then(Commands.argument("homeName", StringArgumentType.word())
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        homeCommands.sethome(new FabricGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                        return 1;
                    })));

            dispatcher.register(Commands.literal("home")
                .then(Commands.argument("homeName", StringArgumentType.word())
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        homeCommands.home(new FabricGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                        return 1;
                    })));

            dispatcher.register(Commands.literal("delhome")
                .then(Commands.argument("homeName", StringArgumentType.word())
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        homeCommands.delhome(new FabricGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                        return 1;
                    })));
        });
    }
}
