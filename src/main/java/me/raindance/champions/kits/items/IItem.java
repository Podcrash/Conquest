package me.raindance.champions.kits.items;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public interface IItem {
    boolean useItem(Player player, Action action);
}
