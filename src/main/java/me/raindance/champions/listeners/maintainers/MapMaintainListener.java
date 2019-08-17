package me.raindance.champions.listeners.maintainers;

import me.raindance.champions.Main;
import me.raindance.champions.damage.DamageQueue;
import me.raindance.champions.listeners.ListenerBase;
import me.raindance.champions.world.WorldManager;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Prevent dumb things from happening
 */
public class MapMaintainListener extends ListenerBase {
    public MapMaintainListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeather(WeatherChangeEvent event){
        if (plugin.getConfig().getList("worlds").contains(event.getWorld().getName()) || evaluate(event.getWorld())) {
            event.getWorld().setWeatherDuration(0);

        }
    }
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (plugin.getConfig().getList("worlds").contains(event.getEntity().getWorld().getName()) || evaluate(event.getEntity().getWorld())) {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (plugin.getConfig().getList("worlds").contains(e.getBlock().getWorld().getName()) || evaluate(e.getBlock().getWorld())) {
            if (!e.getPlayer().hasPermission("champions.can.break.map")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getConfig().getList("worlds").contains(e.getPlayer().getWorld().getName()) || evaluate(e.getPlayer().getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFood(FoodLevelChangeEvent e) {
        if (plugin.getConfig().getList("worlds").contains(e.getEntity().getWorld().getName()) || evaluate(e.getEntity().getWorld())) {
            e.setCancelled(true);
        }else if(e.getEntity().getWorld().getName().equalsIgnoreCase("world")) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickUp(PlayerPickupItemEvent event){
        String name = event.getItem().getCustomName();
        if(name != null && name.startsWith("RITB")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void despawn(ItemDespawnEvent e) {
        if(evaluate(e.getEntity().getWorld())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void beforeDie(EntityDamageEvent event){
        if(event.getEntityType() == EntityType.PLAYER && ((Player) event.getEntity()).getHealth() - event.getDamage() <= 0D) {
            event.setCancelled(true);
            if(event.getCause() != null)
                DamageQueue.artificialDie((Player) event.getEntity());
        }
    }

    @EventHandler
    public void die(PlayerDeathEvent event) {
        Main.getInstance().getLogger().info("from MapMaintainListener#92: If you ever see this message, it's a bug");
    }

    private boolean evaluate(World world) {
        return WorldManager.getInstance().getWorlds().contains(world.getName());
    }
}
