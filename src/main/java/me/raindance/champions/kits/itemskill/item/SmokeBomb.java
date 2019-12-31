package me.raindance.champions.kits.itemskill.item;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

import java.util.List;

@ItemMetaData(mat = Material.FIREWORK_CHARGE)
public class SmokeBomb implements IItem {
    @Override
    public void useItem(Player player, Action action) {
        Location location = player.getLocation();
        World world = location.getWorld();
        Vector vector = location.getDirection();
        vector.multiply(1.5D);
        vector.setY(vector.getY() + 0.2);
        ItemManipulationManager.interceptWithCooldown(player, Material.FIREWORK_CHARGE, player.getLocation(), vector, 1F,
                (item, livingEntity) -> {
                    Location land = item.getLocation();
                    world.playEffect(land, Effect.EXPLOSION_HUGE, 1);
                    List<LivingEntity> entities = world.getLivingEntities();
                    for(LivingEntity entity : entities) {
                        if(entity.getLocation().distanceSquared(land) > 16D) continue;
                        StatusApplier.getOrNew(entity).applyStatus(Status.BLIND, 3, 0);
                        StatusApplier.getOrNew(entity).applyStatus(Status.SLOW, 3, 0);
                    }
                });
    }

    @Override
    public String getName() {
        return "Smoke Bomb";
    }
}
