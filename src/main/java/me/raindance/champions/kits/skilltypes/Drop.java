package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Drop extends Skill {

    @EventHandler
    public final void dropSkill(PlayerDropItemEvent e) {
        if(e.getPlayer() != getPlayer()) return;
        if(!isHolding(e.getItemDrop().getItemStack())) return;
        if(isInWater()) return;
        SkillUseEvent useEvent = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;
        getPlayer().sendMessage(getUsedMessage());
        drop(e);
        e.setCancelled(true);
    }

    public abstract void drop(PlayerDropItemEvent e);

    protected boolean isHolding(@Nonnull ItemStack dropped) {
        if(dropped.getItemMeta() == null || dropped.getItemMeta().getDisplayName() == null) return false;
        ItemType[] weapons = ItemType.details();

        String name = dropped.getItemMeta().getDisplayName().toUpperCase();
        if(getItemType() != ItemType.NULL) {
            return name.contains(getItemType().getName());
        }
        for(ItemType w : weapons) {
            if(name.contains(w.getName())) return true;
        }
        return false;
    }
}
