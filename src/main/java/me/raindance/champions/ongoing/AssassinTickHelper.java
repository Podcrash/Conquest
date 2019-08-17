package me.raindance.champions.ongoing;

import org.bukkit.entity.Player;

public abstract class AssassinTickHelper implements TickHelper {
    private Player player;

    public AssassinTickHelper(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
