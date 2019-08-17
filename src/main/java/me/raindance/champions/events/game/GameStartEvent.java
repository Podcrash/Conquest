package me.raindance.champions.events.game;

import me.raindance.champions.game.Game;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends GameEvent {
    private static final HandlerList handlers = new HandlerList();

    public GameStartEvent(Game game, String message) {
        super(game, message);
    }

    public GameStartEvent(Game game) {
        this(game, "Game " + game.getId() + " has started!");
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
