package me.lordraindance2.sweetdreams;

import com.comphenix.protocol.ProtocolLibrary;
import me.lordraindance2.sweetdreams.tracker.CoordinateTracker;
import me.lordraindance2.sweetdreams.tracker.Tracker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class LunarDance {
    //This can also extend JavaPlugin
    public static JavaPlugin plugin;
    private static CoordinateTracker coordinateTracker;
    private List<Listener> listeners;
    private JoinInject inject;
    private List<Tracker> trackers;

    public void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(inject = new JoinInject(), plugin);
        pluginManager.registerEvents(new Commands(), plugin);
    }

    public void setup(JavaPlugin plugin1) {
        plugin = plugin1;
        registerListeners();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for(Player player : players) {
            inject.inject(player);
        }
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(plugin));
    }

    private void addTracker(Tracker tracker) {
        trackers.add(tracker);
        tracker.enable();
    }

    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(plugin);
        HandlerList.unregisterAll();
        for(Tracker tracker : trackers)
            tracker.disable();
    }

    public static CoordinateTracker getCoordinateTracker() {
        return coordinateTracker;
    }
}
