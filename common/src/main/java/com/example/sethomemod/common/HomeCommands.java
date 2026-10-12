package com.example.sethomemod.common;

public class HomeCommands {
    private final HomeManager homeManager;

    public HomeCommands(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    private String message(String english, String french) {
        return "fr".equals(homeManager.getLanguage()) ? french : english;
    }

    public void sethome(GamePlayer player, String homeName) {
        homeManager.setHome(
            player.getUuid(),
            homeName,
            player.getX(), player.getY(), player.getZ(),
            player.getDimensionId()
        );
        player.sendMessage(message(
            "Home '" + homeName + "' set successfully!",
            "Home '" + homeName + "' défini avec succès !"
        ));
    }

    public void home(GamePlayer player, String homeName) {
        HomeData home = homeManager.getHome(player.getUuid(), homeName);
        if (home == null) {
            player.sendMessage(message(
                "Home '" + homeName + "' does not exist.",
                "Le home '" + homeName + "' n'existe pas."
            ));
            return;
        }
        player.teleport(home.x, home.y, home.z, home.dimensionId);
        player.sendMessage(message(
            "Teleported to '" + homeName + "'!",
            "Téléporté à '" + homeName + "' !"
        ));
    }

    public void delhome(GamePlayer player, String homeName) {
        boolean removed = homeManager.delHome(player.getUuid(), homeName);
        if (removed) {
            player.sendMessage(message(
                "Home '" + homeName + "' deleted.",
                "Home '" + homeName + "' supprimé."
            ));
        } else {
            player.sendMessage(message(
                "Home '" + homeName + "' does not exist.",
                "Le home '" + homeName + "' n'existe pas."
            ));
        }
    }
}
