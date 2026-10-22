package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("sethomemod")
public final class SetHomeModNeoForge {
    private final HomeCommands homeCommands;

    public SetHomeModNeoForge() {
        HomeStorage storage = new HomeStorage(FMLPaths.CONFIGDIR.get().resolve("tpsethome"));
        this.homeCommands = new HomeCommands(new HomeManager(storage));
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("sethome")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    homeCommands.sethome(new NeoForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));
        dispatcher.register(Commands.literal("home")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    homeCommands.home(new NeoForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));
        dispatcher.register(Commands.literal("delhome")
            .then(Commands.argument("homeName", StringArgumentType.word())
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    homeCommands.delhome(new NeoForgeGamePlayer(player), StringArgumentType.getString(context, "homeName"));
                    return 1;
                })));
    }
}
