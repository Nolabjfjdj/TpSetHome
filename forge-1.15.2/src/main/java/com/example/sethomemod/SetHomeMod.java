package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("sethomemod")
public final class SetHomeMod {
    private final HomeCommands homeCommands;

    public SetHomeMod() {
        HomeStorage storage = new HomeStorage(FMLPaths.CONFIGDIR.get().resolve("tpsethome"));
        homeCommands = new HomeCommands(new HomeManager(storage));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();

        dispatcher.register(Commands.literal("sethome")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().asPlayer();
                    homeCommands.sethome(new ForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));

        dispatcher.register(Commands.literal("home")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().asPlayer();
                    homeCommands.home(new ForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));

        dispatcher.register(Commands.literal("delhome")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().asPlayer();
                    homeCommands.delhome(new ForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));
    }
}
