package com.example.sethomemod;

import com.example.sethomemod.common.HomeCommands;
import com.example.sethomemod.common.HomeManager;
import com.example.sethomemod.common.HomeStorage;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.Mod.EventHandler;

import java.nio.file.Paths;

@Mod(modid = "sethomemod", name = "TpSetHome", version = "1.0.0")
public final class SetHomeMod {
    private final HomeCommands homeCommands;

    public SetHomeMod() {
        HomeStorage storage = new HomeStorage(Paths.get("config", "tpsethome"));
        homeCommands = new HomeCommands(new HomeManager(storage));
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new HomeCommand("sethome", homeCommands));
        event.registerServerCommand(new HomeCommand("home", homeCommands));
        event.registerServerCommand(new HomeCommand("delhome", homeCommands));
    }
}
