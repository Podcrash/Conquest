package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Interaction;
import me.raindance.champions.time.resources.TimeResource;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class DwarfToss extends Interaction implements TimeResource{
    private float multiplier;
    private Entity victim = null;
    private HashMap<String, Long> delays = new HashMap<>();
    public DwarfToss(Player player, int level) {
        super(player, "Dwarf Toss", level, SkillType.Brute, ItemType.SWORD, InvType.SWORD, 15 - level);
        this.multiplier = 0.8F + 0.15F * getLevel();
        setDesc(Arrays.asList(
                "Hold block to pick up target player.",
                "Release block to throw target player.",
                "",
                "Players you are holding cannot harm ",
                "you, or be harmed by others."
        ));
    }

    @Override
    public void doSkill(Entity clickedEntity) {
        if(!onCooldown()) {
            getPlayer().setPassenger(clickedEntity);
            victim = clickedEntity;
            victim.playEffect(EntityEffect.HURT);
            this.setLastUsed(System.currentTimeMillis());
            run(1, 0);
        }
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void task() {
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking() || System.currentTimeMillis() - getLastUsed() >= 10000L || ((CraftPlayer) getPlayer()).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() >= 1.3;
    }

    @Override
    public void cleanup() {
        if(victim.leaveVehicle()) {
            victim.playEffect(EntityEffect.HURT);
            delays.put(victim.getName(), System.currentTimeMillis());
            Vector vector = getPlayer().getLocation().getDirection();
            vector.normalize().multiply(multiplier);
            victim.setVelocity(vector);
            victim = null;
        }

    }

    @EventHandler
    public void exit(VehicleExitEvent event) {
        if(event.getExited() == victim) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void hit(DamageApplyEvent event) {
        if(victim != null && event.getAttacker() == victim) {
            event.setCancelled(true);
        }
        if (delays.containsKey(event.getVictim().getName())) {
            String name = event.getVictim().getName();
            long time = delays.get(name);
            if (System.currentTimeMillis() - time >= 600L) delays.remove(name);
            else event.setCancelled(true);
        }
    }
}
