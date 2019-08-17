package me.raindance.champions.kits.skills.AssassinSkills;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IContinuousPassive;
import me.raindance.champions.kits.iskilltypes.IDropPassive;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.TimeHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.LinkedList;

public class Recall extends Passive implements IDropPassive, IContinuousPassive, IPassiveTimer {
    private final int MAX_LEVEL = 3;
    private int duration;
    private double health;

    private final int time;
    private final int shiftTime;
    private final int defaultCooldown;
    private final int shiftCooldown;
    private LinkedList<Location> locations = new LinkedList<>();

    public Recall(Player player, int level) {
        super(player, "Recall", level, SkillType.Assassin, InvType.PASSIVEA, 40 - 5 * level);
        duration = 2 + level;
        health = 1.5d + 0.5d * level;

        time = 3 + level;
        shiftTime = 2;

        defaultCooldown = (int) cooldown;
        shiftCooldown = 15 - level;

        this.setLastUsed(0);
        setDesc("Go back in time %%time%% seconds restoring your location",
                "and giving you Regeneration 3 for %%time%% seconds.",
                "Cannot be used while Slowed.",
                "",
                "Alternatively, hold shift while Recalling to go back 2 seconds",
                "and restore %%health%% health instantly.",
                "Can be used while Slowed.",
                "",
                "Shift Recall Cooldown: %%shiftCooldown%%"
        );
        addDescArg("time", () -> time);
        addDescArg("health", () -> health);
        addDescArg("shiftCooldown", () -> shiftCooldown);
    }

    @Override
    public void start() {
        if (getPlayer() != null) TimeHandler.repeatedTimeSeconds(1, 0L, this);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void drop(PlayerDropItemEvent e) {
        if (checkItem(e.getItemDrop().getItemStack()) && !isInWater() && e.getPlayer() == getPlayer()) {
            doSkill();
        }
    }

    public void doSkill() {
        if(!onCooldown()) {
            this.getPlayer().sendMessage(getUsedMessage());
            SkillUseEvent useEvent = new SkillUseEvent(this);
            Bukkit.getPluginManager().callEvent(useEvent);
            if (useEvent.isCancelled()) return;
            boolean sneaking = getPlayer().isSneaking();
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
            if (sneaking) {
                shiftRecall();
            } else {
                recall();
            }
            setCooldown(sneaking);
            this.setLastUsed(System.currentTimeMillis());
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
            getPlayer().setFallDistance(0);
        } else this.getPlayer().sendMessage(getCooldownMessage());
    }


    private void shiftRecall() {
        try {
            getPlayer().teleport(locations.get(shiftTime));
        } catch (IndexOutOfBoundsException e) {
            getPlayer().teleport(locations.get(locations.size() - 1));
        }
        double heal = (getPlayer().getHealth() + health > 20) ? 20 : (getPlayer().getHealth() + health);
        getPlayer().setHealth(heal);

    }

    private void recall() {
        try {
            getPlayer().teleport(locations.get(time));
        } catch (IndexOutOfBoundsException e) {
            getPlayer().teleport(locations.get(locations.size() - 1));
        }
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.REGENERATION, duration, 2);
    }

    private void setCooldown(boolean isSneaking) {
        cooldown = isSneaking ? shiftCooldown : defaultCooldown;
    }
    /*
    Record locations
     */

    @Override
    public void task() {
        if (locations.size() > time + 1) this.locations.removeLast();
        this.locations.addFirst(getPlayer().getLocation());
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
