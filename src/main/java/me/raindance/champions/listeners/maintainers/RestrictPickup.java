package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
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
}
