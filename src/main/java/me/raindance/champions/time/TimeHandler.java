package me.raindance.champions.time;

import me.raindance.champions.Main;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TimeHandler {
    private static final JavaPlugin plugin = Main.instance;
    private static HashMap<TimeResource, List<Integer>> timeRunMap = new HashMap<>();

    private TimeHandler() {

    }

    public static void repeatedTime(long ticks, long delayTicks, TimeResource resource) {
        Runnable runnable = () -> {
            if (resource.cancel()) {
                resource.cleanup();
                unregister(resource);
            } else resource.task();

        };
        //Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable).getTaskId();
        int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delayTicks, ticks);
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void repeatedTimeAsync(long ticks, long delayTicks, TimeResource resource) {
        Runnable runnable = () -> {
            if (resource.cancel()) {
                resource.cleanup();
                unregister(resource);
            } else resource.task();

        };
        int taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, ticks).getTaskId();
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void repeatedTimeSeconds(long seconds, long delaySeconds, TimeResource resource) {
        Runnable runnable = () -> {
            resource.task();
            if (resource.cancel()) {
                unregister(resource);
                resource.cleanup();
            }
        };
        int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delaySeconds * 20, seconds * 20);
        //runnable.runTaskTimer(plugin, delaySeconds * 20, seconds * 20);
        register(resource, taskID);
    }

    public static void delayTime(long delay, TimeResource resource) {
        Runnable runnable = () -> {
            resource.task();
            if (resource.cancel()) {
                unregister(resource);
                resource.cleanup();
            }
        };
        int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        register(resource, taskID);
        //runnable.scheduleSync(plugin, delay * 20);
    }

    private static void register(TimeResource resource, int taskID) {
        if (timeRunMap.get(resource) == null) {
            List<Integer> newList = new ArrayList<>();
            newList.add(taskID);
            timeRunMap.put(resource, newList);
        } else {
            timeRunMap.get(resource).add(taskID);
        }
    }

    public static void unregister(TimeResource resource) {
        if (timeRunMap.containsKey(resource)) {
            timeRunMap.get(resource).forEach((taskID) -> Bukkit.getScheduler().cancelTask(taskID));
            timeRunMap.remove(resource, timeRunMap.get(resource));
        }
    }

    public static void forceDestroy(TimeResource resource) {
        if (timeRunMap.containsKey(resource)) {
            timeRunMap.remove(resource, timeRunMap.get(resource));
        }
        timeRunMap.get(resource).forEach((taskID) -> Bukkit.getScheduler().cancelTask(taskID));
    }

    public static HashMap<TimeResource, List<Integer>> getTimeRunMap() {
        return timeRunMap;
    }

}
