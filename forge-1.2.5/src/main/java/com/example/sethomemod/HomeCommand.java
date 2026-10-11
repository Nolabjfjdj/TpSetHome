package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommand;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.EntityPlayerMP;

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
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendChatToPlayer("Cette commande doit être utilisée par un joueur.");
            return;
        }
        if (args.length != 1) {
            sender.sendChatToPlayer(getCommandUsage(sender));
            return;
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

    @Override
    public int compareTo(Object other) {
        if (other instanceof ICommand) {
            return commandName.compareTo(((ICommand) other).getCommandName());
        }
        return 0;
    }
}
