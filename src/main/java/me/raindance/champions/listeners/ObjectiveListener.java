package me.raindance.champions.listeners;

import com.podcrash.api.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ObjectiveListener extends ListenerBase {
    public ObjectiveListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

    }
}
