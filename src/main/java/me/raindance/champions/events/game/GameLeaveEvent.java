package me.raindance.champions.events.game;

import me.raindance.champions.game.Game;
import org.bukkit.entity.Player;

public class GameLeaveEvent extends GamePlayerEvent {
    public GameLeaveEvent(Game game, Player who) {
        super(game, who, "message");
    }
}
