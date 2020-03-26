package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillInteractEvent;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

//TODO: remove ICooldown dependency
public abstract class Interaction extends Skill implements ICooldown {
    protected boolean canMiss = true;
    private boolean landed = false;

    public Interaction() {
        super();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void rightClick(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if(!(entity instanceof LivingEntity)) return;
        if (event.getPlayer() != getPlayer() || !isHolding()) return;
        if (isInWater()) {
            //getPlayer().sendMessage(getWaterMessage());
            return;
        }
        if(onCooldown()) return;
        LivingEntity victim = (LivingEntity) entity;
        SkillUseEvent useEvent = new SkillInteractEvent(this, victim);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;
        doSkill(victim);
        if (landed) {
            getPlayer().sendMessage(getUsedMessage(victim));
        }
    }

    public abstract void doSkill(LivingEntity clickedEntity);

    public void landed() {
        landed = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                landed = false;
            }
        }.runTaskLater(Main.instance, 0L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void miss(PlayerInteractEvent event) {
        if (event.getPlayer() == getPlayer() && isHolding() && rightClickCheck(event.getAction())) {
            if(isInWater()) getPlayer().sendMessage(getWaterMessage());
            if(!canMiss) return;
            if (!onCooldown()) {
                if (!landed && !isInWater()) {
                    getPlayer().sendMessage(String.format("%s%s> %sYou missed %s%s%s.",
                            ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, ChatColor.GREEN, getName(),ChatColor.GRAY));
                    this.setLastUsed(System.currentTimeMillis());
                    missSkill();
                }
            }
        }
    }

    /**
     * Override this if necessary
     */
    public void missSkill() {

    }
}
