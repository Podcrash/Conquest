package me.raindance.champions.events.game;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.TeamEnum;
import org.bukkit.entity.Player;

public class GameJoinEvent extends GamePlayerEvent {
    public GameJoinEvent(Game game, Player who) {
        super(game, who, "message");
    }
}
