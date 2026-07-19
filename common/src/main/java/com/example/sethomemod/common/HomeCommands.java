package com.example.sethomemod.common;

public class HomeCommands {
    private final HomeManager homeManager;

    public HomeCommands(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    public void sethome(GamePlayer player, String homeName) {
        homeManager.setHome(
            player.getUuid(),
            homeName,
            player.getX(), player.getY(), player.getZ(),
            player.getDimensionId()
        );
        player.sendMessage("Home '" + homeName + "' défini avec succès !");
    }

    public void home(GamePlayer player, String homeName) {
        HomeData home = homeManager.getHome(player.getUuid(), homeName);
        if (home == null) {
            player.sendMessage("Le home '" + homeName + "' n'existe pas.");
            return;
        }
        player.teleport(home.x, home.y, home.z, home.dimensionId);
        player.sendMessage("Téléporté à '" + homeName + "' !");
    }

    public void delhome(GamePlayer player, String homeName) {
        boolean removed = homeManager.delHome(player.getUuid(), homeName);
        if (removed) {
            player.sendMessage("Home '" + homeName + "' supprimé.");
        } else {
            player.sendMessage("Le home '" + homeName + "' n'existe pas.");
        }
    }
}
