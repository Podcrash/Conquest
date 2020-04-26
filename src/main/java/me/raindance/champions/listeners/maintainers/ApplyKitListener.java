package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.listeners.ListenerBase;
import me.raindance.champions.events.ApplyKitEvent;
import me.raindance.champions.listeners.PlayerJoinEventTest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ApplyKitListener extends ListenerBase {
    public ApplyKitListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void kit(ApplyKitEvent e) {
        if(e.getChampionsPlayer().getPlayer().getWorld().getName().equals("world")) {
            e.setKeepInventory(true);
            e.getChampionsPlayer().getInventory().setItem(35, PlayerJoinEventTest.beacon);
        }
    }
}
