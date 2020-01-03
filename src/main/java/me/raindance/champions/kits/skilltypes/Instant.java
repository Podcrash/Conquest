package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class Instant extends Skill {
    private final Skill instance;
    public Instant() {
        super();
        instance = this;
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void interact(PlayerInteractEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!canUseSkill(e)) return;
                SkillUseEvent useEvent = new SkillUseEvent(instance);
                Bukkit.getPluginManager().callEvent(useEvent);
                if (useEvent.isCancelled()) return;
                doSkill(e, e.getAction());
            }
        }.runTask(Main.instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractAtEntityEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!canUseSkill(e)) return;
                SkillUseEvent useEvent = new SkillUseEvent(instance);
                Bukkit.getPluginManager().callEvent(useEvent);
                if (useEvent.isCancelled()) return;
                Main.getInstance().log.info("You arne't supposed to see this message");
            }
        }.runTask(Main.instance);
    }

    public boolean canUseSkill(PlayerEvent event) {
        if (getPlayer() != event.getPlayer() || !isHolding()) return false;

        if (isInWater()) {
            getPlayer().sendMessage(getWaterMessage());
            return false;
        } else return true;
    }


    protected abstract void doSkill(PlayerInteractEvent event, Action action);
}