package me.raindance.champions.kits.itemskill.item;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.annotation.ItemMetaData;
import me.raindance.champions.kits.itemskill.IItem;
import me.raindance.champions.kits.itemskill.TrapItem;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//when there's a better item system, change this
@ItemMetaData(mat = Material.REDSTONE_LAMP_OFF, actions = {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK})
public class StunCharge extends TrapItem {
    public StunCharge() {
        super(2500);
    }
    @Override
    public Item throwItem(Player player, Action action) {
        Location location = player.getLocation();
        Vector direction = location.getDirection();
        Vector vector = new Vector(0, 0, 0);
        if(isLeft(action)) vector = throwVector(direction);

        return ItemManipulationManager.regular(Material.REDSTONE_LAMP_OFF, location, vector);
    }

    @Override
    protected void primeTrap(Item item) {
        AbstractPacket packet2 = ParticleGenerator.createBlockEffect(item.getLocation().toVector(), Material.OBSIDIAN.getId());
        for(Player p : item.getWorld().getPlayers()) {
            packet2.sendPacket(p);
        }
        SoundPlayer.sendSound(item.getLocation(), "dig.stone", 1.25F, 66);
    }

    @Override
    protected void snareTrap(Player owner, Player player, Item item) {
        Location land = item.getLocation();
        World world = land.getWorld();
        world.strikeLightningEffect(land);
        List<LivingEntity> entities = world.getLivingEntities();
        for (LivingEntity entity : entities) {
            if (entity.getLocation().distanceSquared(land) > 4D) continue;
            StatusApplier.getOrNew(entity).applyStatus(Status.SILENCE, 4, 0);
            StatusApplier.getOrNew(entity).applyStatus(Status.SHOCK, 4, 0);
        }
    }

    @Override
    public String getName() {
        return "Stun Charge";
    }
}
