package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod("sethomemod")
public class SetHomeMod {

    private final HomeCommands homeCommands;

        public SetHomeMod() {
                Path storageDir = FMLPaths.CONFIGDIR.get().resolve("tpsethome");
                        HomeStorage storage = new HomeStorage(storageDir);
                                HomeManager manager = new HomeManager(storage);
                                        this.homeCommands = new HomeCommands(manager);

                                                MinecraftForge.EVENT_BUS.register(this);
                                                    }

                                                        @SubscribeEvent
                                                            public void onRegisterCommands(RegisterCommandsEvent event) {
                                                                    CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

                                                                            dispatcher.register(Commands.literal("sethome")
                                                                                        .then(Commands.argument("homeName", StringArgumentType.word())
                                                                                                        .executes(context -> {
                                                                                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                                                                                                                String homeName = StringArgumentType.getString(context, "homeName");
                                                                                                                                                                    homeCommands.sethome(new ForgeGamePlayer(player), homeName);
                                                                                                                                                                                        return 1;
                                                                                                                                                                                                        })));

                                                                                                                                                                                                                dispatcher.register(Commands.literal("home")
                                                                                                                                                                                                                            .then(Commands.argument("homeName", StringArgumentType.word())
                                                                                                                                                                                                                                            .executes(context -> {
                                                                                                                                                                                                                                                                ServerPlayer player = context.getSource().getPlayerOrException();
                                                                                                                                                                                                                                                                                    String homeName = StringArgumentType.getString(context, "homeName");
                                                                                                                                                                                                                                                                                                        homeCommands.home(new ForgeGamePlayer(player), homeName);
                                                                                                                                                                                                                                                                                                                            return 1;
                                                                                                                                                                                                                                                                                                                                            })));

                                                                                                                                                                                                                                                                                                                                                    dispatcher.register(Commands.literal("delhome")
                                                                                                                                                                                                                                                                                                                                                                .then(Commands.argument("homeName", StringArgumentType.word())
                                                                                                                                                                                                                                                                                                                                                                                .executes(context -> {
                                                                                                                                                                                                                                                                                                                                                                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                                                                                                                                                                                                                                                                                                                                                                                        String homeName = StringArgumentType.getString(context, "homeName");
                                                                                                                                                                                                                                                                                                                                                                                                                                            homeCommands.delhome(new ForgeGamePlayer(player), homeName);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                return 1;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                })));
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    }