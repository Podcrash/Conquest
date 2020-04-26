package me.raindance.champions.kits.itemskill;

import com.podcrash.api.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public interface IItem extends DamageSource {
    /**
     *
     * @param player the player who uses it
     * @param action although we clarify with an interface, this part is required if you want to make multiple things do different stuff
     */
    void useItem(Player player, Action action);


    default Vector throwVector(Vector direction) {
        direction.normalize().multiply(1.2);
        direction.setY(direction.getY() + 0.15);

        return direction;
    }

    default boolean isLeft(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }

    default boolean isRight(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
