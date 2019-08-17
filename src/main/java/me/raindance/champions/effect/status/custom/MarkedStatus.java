package me.raindance.champions.effect.status.custom;

import me.raindance.champions.effect.status.Status;
import org.bukkit.entity.Player;

public class MarkedStatus extends CustomStatus {

    public MarkedStatus(Player player) {
        super(player, Status.MARKED);
    }

    @Override
    protected void doWhileAffected() {

    }

    @Override
    protected void removeEffect() {
        getApplier().removeMark();
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isMarked();
    }
}
