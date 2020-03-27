package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Drop extends Skill {

    @EventHandler
    public final void dropSkill(PlayerDropItemEvent e) {
        if(e.getPlayer() != getPlayer()) return;
        if(!isHolding(e.getItemDrop().getItemStack())) return;
        if(isInWater()) {
            e.getPlayer().sendMessage(getWaterMessage());
            return;
        }
        //check for cooldown
        if(this instanceof ICooldown) {
            ICooldown cooldownSkill = (ICooldown) this;
            if(cooldownSkill.onCooldown()) {
                getPlayer().sendMessage(cooldownSkill.getCooldownMessage());
                return;
            }
        }

        SkillUseEvent useEvent = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;
        drop(e);
        e.setCancelled(true);
    }

    /**
     * true if the skill is used.
     * @param e
     * @return
     */
    public abstract boolean drop(PlayerDropItemEvent e);

    protected boolean isHolding(@Nonnull ItemStack dropped) {
        ItemType[] weapons = ItemType.details();

        String name = dropped.getType().name().toUpperCase();;
        if(getItemType() != ItemType.NULL) {
            return name.contains(getItemType().getName());
        }
        for(ItemType w : weapons) {
            if(name.contains(w.getName())) return true;
        }
        return false;
    }
}
