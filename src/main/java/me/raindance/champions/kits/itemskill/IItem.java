package me.raindance.champions.kits.itemskill;

import com.podcrash.api.mc.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

public interface IItem extends DamageSource {
    /**
     *
     * @param player the player who uses it
     * @param action although we clarify with an interface, this part is required if you want to make multiple things do different stuff
     */
    void useItem(Player player, Action action);
}
