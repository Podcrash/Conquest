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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class Instant extends Skill {
    private final Skill instance;
    protected boolean rightClickSkill = true;
    public Instant() {
        super();
        instance = this;
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void interact(PlayerInteractEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getPlayer() == e.getPlayer()) {
                    if (isHolding()) {
                        if (!isInWater()) {
                            SkillUseEvent useEvent = new SkillUseEvent(instance);
                            Bukkit.getPluginManager().callEvent(useEvent);
                            if (useEvent.isCancelled()) return;
                            doSkill(e, e.getAction());
                        } else if(rightClickSkill && rightClickCheck(e.getAction()))
                            getPlayer().sendMessage(getWaterMessage());
                    }
                }
            }
        }.runTask(Main.instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractAtEntityEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getPlayer() == e.getPlayer()) {
                    if (isHolding()) {
                        if (!isInWater()) {
                            SkillUseEvent useEvent = new SkillUseEvent(instance);
                            Bukkit.getPluginManager().callEvent(useEvent);
                            if (useEvent.isCancelled()) return;
                            Main.getInstance().log.info("You arne't supposed to see this message");
                        } else getPlayer().sendMessage(getWaterMessage());
                    }
                }
            }
        }.runTask(Main.instance);
    }


    protected abstract void doSkill(PlayerInteractEvent event, Action action);
}