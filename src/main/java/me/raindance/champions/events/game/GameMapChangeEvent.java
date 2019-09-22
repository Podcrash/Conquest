package me.raindance.champions.events.game;

import me.raindance.champions.game.Game;
import org.bukkit.event.HandlerList;

public class GameMapChangeEvent extends GameEvent {
    private String map;
    private static final HandlerList handlers = new HandlerList();
    public GameMapChangeEvent(Game game, String map) {
        super(game, "Map changed");
        this.map = map;
    }

    public String getMap() {
        return map;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
