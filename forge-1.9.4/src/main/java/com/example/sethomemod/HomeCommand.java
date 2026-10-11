package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public final class HomeCommand extends CommandBase {
    private final String commandName;
    private final HomeCommands homeCommands;

    public HomeCommand(String commandName, HomeCommands homeCommands) {
        this.commandName = commandName;
        this.homeCommands = homeCommands;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + commandName + " <nom>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            throw new CommandException("Cette commande doit être utilisée par un joueur.");
        }
        if (args.length != 1) {
            throw new CommandException(getCommandUsage(sender));
        }

        ForgeGamePlayer player = new ForgeGamePlayer((EntityPlayerMP) sender);
        if ("sethome".equals(commandName)) {
            homeCommands.sethome(player, args[0]);
        } else if ("home".equals(commandName)) {
            homeCommands.home(player, args[0]);
        } else if ("delhome".equals(commandName)) {
            homeCommands.delhome(player, args[0]);
        }
    }
}
