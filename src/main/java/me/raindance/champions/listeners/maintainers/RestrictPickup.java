package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class RestrictPickup extends ListenerBase {
    public RestrictPickup(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void stopPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void stopBreak(BlockBreakEvent e) {
        e.setCancelled(true);
        if (!e.getPlayer().hasPermission("invicta.map"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void stopPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
        if (!e.getPlayer().hasPermission("invicta.map"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void preventCraft(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked != null && clicked.getType() == InventoryType.CRAFTING)
            event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFood(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

}
