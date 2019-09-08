package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class Void extends TogglePassive implements TimeResource, IEnergy {
    private final StatusApplier statusApplier;
    private final int damage;
    private final int manaCost;
    public Void(Player player, int level) {
        super(player, "Void", level, "boom", SkillType.Mage, InvType.PASSIVEA);
        statusApplier = (player == null) ? null : StatusApplier.getOrNew(getPlayer());
        damage = 1 + level;
        manaCost = 8 - level;
        setDesc(Arrays.asList(
                "Drop Axe/Sword to Toggle. ",
                "",
                "While in void form, you receive ",
                "Slow 3 and take no knockback. ",
                "",
                "Reduces incoming damage by %%reduction%%, but ",
                "burns %%energy%% Energy per 1 damage reduced. ",
                "",
                "Energy: %%energydrain%% per Second"
        ));
        addDescArg("reduction", () ->  damage);
        addDescArg("energy", () -> manaCost);
        addDescArg("energydrain", this::getEnergyUsage);
    }


    @Override
    public int getEnergyUsage() {
        return 3;
    }

    @Override
    public void toggle() {
        if(isToggled()) {
            if(hasEnergy(manaCost)) {
                useEnergy(manaCost);
                run(1, 0);
            }
        }else {
            getChampionsPlayer().getEnergyBar().toggleRegen(true);
            unregister();
        }
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void task() {
        statusApplier.applyStatus(Status.SLOW, 120F, 2);
        statusApplier.applyStatus(Status.INVISIBILITY, 120F, 2);
        Location location = getPlayer().getLocation();
        location.getWorld().playSound(location, Sound.BLAZE_BREATH, 0.3f, 0.5f);
        if(hasEnergy(getEnergyUsageTicks())) {
            useEnergy(getEnergyUsageTicks());
            getChampionsPlayer().getEnergyBar().toggleRegen(false);
        } else {
            forceToggle();
        }
        if(getChampionsPlayer().isSilenced()){
            forceToggle();
        }
    }

    @Override
    public boolean cancel() {
        return !isToggled();
    }

    @Override
    public void cleanup() {
        statusApplier.removeStatus(Status.SLOW, Status.INVISIBILITY);
        getChampionsPlayer().getEnergyBar().toggleRegen(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if(isToggled() && event.getVictim() == getPlayer()) {
            event.setModified(true);
            int reduction = damage;
            double total = event.getDamage() - damage;
            if(total < 0) {
                reduction += total;
                total = 0;
            }
            double cost = manaCost * reduction;
            if(hasEnergy(cost)) {
                useEnergy(cost);
                event.setDamage(total);
                event.setDoKnockback(false);
                Location location = getPlayer().getLocation();
                location.getWorld().playSound(location, Sound.BLAZE_BREATH, 2f, 1f);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hit(EntityDamageEvent event) {
        if(!isToggled()) return;
        if(event.getCause() == null) return;
        double total = event.getDamage() - damage;
        if(total < 0) total = 0;
        if(hasEnergy(manaCost)) {
            useEnergy(manaCost);
            event.setDamage(total);
            Location location = getPlayer().getLocation();
            location.getWorld().playSound(location, Sound.BLAZE_BREATH, 2f, 1f);
        }
    }

}
