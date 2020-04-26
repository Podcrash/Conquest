package me.raindance.champions.listeners;

import com.podcrash.api.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MapListener extends ListenerBase {
    public MapListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTouch(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTouch(BlockBurnEvent e) {
        e.setCancelled(true);
    }
}
