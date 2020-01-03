package me.raindance.champions.kits.skills.thief;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.location.Coordinate;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.*;
import me.raindance.champions.kits.skilltypes.Drop;
import com.podcrash.api.mc.time.TimeHandler;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.LinkedList;

@SkillMetadata(skillType = SkillType.Thief, invType = InvType.DROP)
public class Recall extends Drop implements ICooldown, IContinuousPassive, IPassiveTimer, IConstruct {
    private double health;

    private final int time;
    private LinkedList<Coordinate> locations = new LinkedList<>();

    @Override
    public float getCooldown() {
        return 30;
    }

    @Override
    public String getName() {
        return "Recall";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    public Recall() {
        health = 4;
        time = 3;
    }

    @Override
    public void afterConstruction() {
        this.setLastUsed(0);
    }

    @Override
    public void start() {
        if (getPlayer() != null) TimeHandler.repeatedTimeSeconds(1, 0L, this);
    }

    public boolean drop(PlayerDropItemEvent e) {
        if(!onCooldown()) {
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
            Location start = getPlayer().getLocation();
            recall();
            Location end = getPlayer().getLocation();
            WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(EnumWrappers.Particle.SPELL_WITCH, 3);
            ParticleGenerator.generateLocAs(packet, start, end);
            this.setLastUsed(System.currentTimeMillis());
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
            getPlayer().setFallDistance(0);
            return true;
        } else this.getPlayer().sendMessage(getCooldownMessage());
        return false;
    }


    private void recall() {
        Location current = getPlayer().getLocation();
        Location newLoc = locations.get(time).toLocation(getPlayer().getWorld());
        newLoc.setPitch(current.getPitch());
        newLoc.setYaw(current.getYaw());
        getPlayer().teleport(newLoc);
        getChampionsPlayer().heal(health);
    }
    /*
    Record locations
     */

    @Override
    public void task() {
        if (locations.size() > time + 1) this.locations.removeLast();
        this.locations.addFirst(Coordinate.from(getPlayer().getLocation()));
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {
        locations = null;
    }
}
