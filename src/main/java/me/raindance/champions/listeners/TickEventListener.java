package me.raindance.champions.listeners;

import com.podcrash.api.events.TickEvent;
import com.podcrash.api.listeners.ListenerBase;
import me.raindance.champions.ongoing.TickHelper;
import me.raindance.champions.ongoing.TickManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TickEventListener extends ListenerBase {
    public TickEventListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void tick(TickEvent e) {
        for(TickHelper helper : TickManager.getHelpers()){
            helper.action();
        }
    }
}

