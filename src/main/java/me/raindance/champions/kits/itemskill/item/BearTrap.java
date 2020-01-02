package me.raindance.champions.kits.itemskill.item;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import me.raindance.champions.kits.itemskill.ItemListener;
import org.bukkit.Bukkit;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//when there's a better item system, change this
@ItemMetaData(mat = Material.STONE_PLATE, actions = {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK})
public class BearTrap implements IItem, ItemListener {
    private static Map<Integer, String> ownerItem;
    private BearTrapProxy proxy;
    public BearTrap() {
        proxy = new BearTrapProxy(this);
        ownerItem = new HashMap<>();
    }
    @Override
    public void useItem(Player player, Action action) {
        Location location = player.getLocation();
        Vector vector = location.getDirection();
        vector.multiply(0);
        Item item = ItemManipulationManager.spawnItem(Material.STONE_PLATE, location);
        item.setPickupDelay(100);
        TimeHandler.delayTime(99, () -> {
            AbstractPacket packet2 = ParticleGenerator.createBlockEffect(item.getLocation().toVector(), Material.OBSIDIAN.getId());
            for(Player p : item.getWorld().getPlayers()) {
                packet2.sendPacket(p);
            }
            SoundPlayer.sendSound(item.getLocation(), "dig.stone", 1.25F, 66);
        });
        ownerItem.put(item.getEntityId(), player.getName());
    }

    @Override
    public Listener getHelperListener() {
        return proxy;
    }

    @Override
    public String getName() {
        return "Stun Charge";
    }

    private static class BearTrapProxy implements Listener {
        private BearTrap instance;
        public BearTrapProxy(BearTrap bearTrap) {
            instance = bearTrap;
        }

        @EventHandler
        public void itemPickUp(PlayerPickupItemEvent e) {
            Item item = e.getItem();
            String ownerName = ownerItem.getOrDefault(item.getEntityId(), null);
            if(ownerName == null) return;
            Player owner = Bukkit.getPlayer(ownerName);

            e.setCancelled(true);
            Location land = item.getLocation();
            World world = land.getWorld();
            List<LivingEntity> entities = world.getLivingEntities();
            for (LivingEntity entity : entities) {
                if (entity.getLocation().distanceSquared(land) > 3D) continue;
                DamageApplier.damage(entity, owner, 3, instance, false);
                StatusApplier.getOrNew(entity).applyStatus(Status.ROOTED, 3, 0);
            }

            item.remove();
        }
    }
}
