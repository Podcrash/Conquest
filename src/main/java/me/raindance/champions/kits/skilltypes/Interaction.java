package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Interaction extends Skill implements ICooldown {
    private boolean hit = false;

    public Interaction() {
        super();
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void rightClick(PlayerInteractAtEntityEvent event) {
        if (event.getPlayer() == getPlayer() && isHolding()) {
            if (!isInWater()) {
                SkillUseEvent useEvent = new SkillUseEvent(this);
                Bukkit.getPluginManager().callEvent(useEvent);
                if (useEvent.isCancelled()) return;
                Bukkit.getScheduler().runTask(Main.instance, () -> doSkill(event.getRightClicked()));
                hit = true;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        hit = false;
                    }
                }.runTaskLater(Main.instance, 1L);
            } else if (!onCooldown()) getPlayer().sendMessage(getWaterMessage());
        }
    }

    public abstract void doSkill(Entity clickedEntity);

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void miss(PlayerInteractEvent event) {
        if (event.getPlayer() == getPlayer() && isHolding() && rightClickCheck(event.getAction())) {
            if (!onCooldown()) {
                if (!hit && !isInWater()) {
                    getPlayer().sendMessage(String.format("%sSkill> %sYou missed %s%s%s.",
                            ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getName(),ChatColor.GRAY));
                    this.setLastUsed(System.currentTimeMillis());
                }
            }
        }
    }

    public String getUsedMessage(Player player) {
        return String.format("%sSkill> %sYou can use %s%s %son %s%s.",
                ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY, ChatColor.YELLOW, player.getName());
    }
}
