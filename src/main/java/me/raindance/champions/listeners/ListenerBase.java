package me.raindance.champions.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerBase implements Listener {
    protected JavaPlugin plugin;

    public ListenerBase(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().info(String.format("[LISTENER] Loading in %s", this.getClass().getSimpleName()));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}
