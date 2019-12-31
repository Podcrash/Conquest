package me.raindance.champions.kits.itemskill.item;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import me.raindance.champions.kits.itemskill.ItemListener;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.util.List;

//when there's a better item system, change this
@ItemMetaData(mat = Material.REDSTONE_LAMP_OFF, actions = {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK})
public class StunCharge implements IItem, ItemListener {
    private StunChargeProxy proxy;
    public StunCharge() {
        proxy = new StunChargeProxy();
    }
    @Override
    public void useItem(Player player, Action action) {
        Location location = player.getLocation();
        Vector vector = location.getDirection();
        vector.multiply(0);
        Item item = ItemManipulationManager.spawnItem(Material.REDSTONE_LAMP_ON, location);
        item.setPickupDelay(10000);
    }

    @Override
    public Listener getHelperListener() {
        return proxy;
    }

    @Override
    public String getName() {
        return "Stun Charge";
    }

    private static class StunChargeProxy implements Listener {
        @EventHandler
        public void itemPickUp(PlayerPickupItemEvent e) {
            e.setCancelled(true);
            Item item = e.getItem();
            Location land = item.getLocation();
            World world = land.getWorld();
            world.strikeLightningEffect(land);
            List<LivingEntity> entities = world.getLivingEntities();
            for (LivingEntity entity : entities) {
                if (entity.getLocation().distanceSquared(land) > 4D) continue;
                StatusApplier.getOrNew(entity).applyStatus(Status.SILENCE, 4, 0);
                StatusApplier.getOrNew(entity).applyStatus(Status.SHOCK, 4, 0);
            }

            item.remove();
        }
    }
}
