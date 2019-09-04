package me.raindance.champions.effect.status.custom;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import org.bukkit.entity.Player;

/**
 * Part of the respawn for games
 * @see me.raindance.champions.listeners.maintainers.GameListener#onDeath(me.raindance.champions.events.DeathApplyEvent)
 */
public class IneptStatus extends CustomStatus {
    private Game game;
    public IneptStatus(Player player) {
        super(player, Status.INEPTITUDE);
        game = GameManager.getGame();
    }

    @Override
    protected void doWhileAffected() {
        for(Player player : game.getPlayers()){
            if(player != getPlayer() && player.canSee(getPlayer())) player.hidePlayer(getPlayer());
        }
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isInept();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeInept();
        for(Player player : game.getPlayers()){
            if(player != getPlayer()) player.showPlayer(getPlayer());
        }
    }
}
