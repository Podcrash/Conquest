package me.raindance.champions.kits.itemskill.item;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import org.bukkit.*;
import org.bukkit.entity.Item;
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
        Vector vector = location.getDirection();
        vector.multiply(0.9D);
        vector.setY(vector.getY() + 0.2);
        ItemManipulationManager.intercept(player, Material.FIREWORK_CHARGE, player.getEyeLocation(), vector, this::bomb);
    }

    private void bomb(Item item, LivingEntity intercepted) {
        Location land = item.getLocation();
        World world = item.getWorld();
        world.playEffect(land, Effect.EXPLOSION_HUGE, 1);
        world.playSound(land, Sound.FIZZ, 2f, 0.5f);
        List<LivingEntity> entities = world.getLivingEntities();
        for(LivingEntity entity : entities) {
            if(entity.getLocation().distanceSquared(land) > 16D) continue;
            StatusApplier.getOrNew(entity).applyStatus(Status.BLIND, 3, 0);
            StatusApplier.getOrNew(entity).applyStatus(Status.SLOW, 3, 0);
        }
        item.remove();
    }
    @Override
    public String getName() {
        return "Smoke Bomb";
    }
}
