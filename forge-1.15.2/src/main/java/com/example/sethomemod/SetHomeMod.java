package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
        event.registerServerCommand(new HomeCommand("sethome", homeCommands, HomeCommand.Action.SET));
        event.registerServerCommand(new HomeCommand("home", homeCommands, HomeCommand.Action.GO));
        event.registerServerCommand(new HomeCommand("delhome", homeCommands, HomeCommand.Action.DELETE));
    }

    private static final class HomeCommand extends CommandBase {
        private enum Action { SET, GO, DELETE }

        private final String name;
        private final HomeCommands commands;
        private final Action action;

        private HomeCommand(String name, HomeCommands commands, Action action) {
            this.name = name;
            this.commands = commands;
            this.action = action;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/" + name + " <nom>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (!(sender instanceof ServerPlayerEntity)) {
                throw new CommandException("Cette commande doit être exécutée par un joueur.");
            }
            if (args.length != 1) {
                throw new CommandException(getUsage(sender));
            }
            ForgeGamePlayer player = new ForgeGamePlayer((ServerPlayerEntity) sender);
            if (action == Action.SET) {
                commands.sethome(player, args[0]);
            } else if (action == Action.GO) {
                commands.home(player, args[0]);
            } else {
                commands.delhome(player, args[0]);
            }
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public List<String> getAliases() {
            return Collections.emptyList();
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender instanceof ServerPlayerEntity;
        }

        @Override
        public boolean isUsernameIndex(String[] args, int index) {
            return false;
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            return Collections.emptyList();
        }
    }
}
