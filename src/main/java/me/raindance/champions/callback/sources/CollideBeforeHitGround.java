package me.raindance.champions.callback.sources;

import me.raindance.champions.callback.CallbackAction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CollideBeforeHitGround extends CallbackAction<CollideBeforeHitGround> {
    private Player player;

    public CollideBeforeHitGround(Player player, long delay) {
        super(delay, 1);
        this.player = player;
        this.changeEvaluation(() -> (
                player.getNearbyEntities(1.15, 1.15, 1.15).size() > 0) ||
                (((Entity) player).isOnGround()));
    }
    public CollideBeforeHitGround(Player player) {
        this(player, 1);
    }
}
