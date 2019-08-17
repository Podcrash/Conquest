package me.raindance.champions.inventory.update;

import org.bukkit.inventory.Inventory;

public interface IUpdateInv {
    Inventory getInventory();
    void update();
}
