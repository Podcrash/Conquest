package me.raindance.champions.listeners.maintainers;

import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class ArrowNerf extends ListenerBase {
    public ArrowNerf(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void damage(DamageApplyEvent event) {

    }
}
