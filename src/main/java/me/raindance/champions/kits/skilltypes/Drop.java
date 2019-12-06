package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Drop extends Skill {

    @EventHandler
    public void dropSkill(PlayerDropItemEvent e) {
        if(e.getPlayer() != getPlayer()) return;
        if(!isHolding()) return;
        if(!isInWater()) return;
        SkillUseEvent useEvent = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;
        drop(e);
    }

    public abstract void drop(PlayerDropItemEvent e);

    @Override
    protected boolean isHolding() {
        ItemType[] weapons = ItemType.details();

        ItemStack holding = getPlayer().getItemInHand();
        String name = holding.getItemMeta().getDisplayName().toUpperCase();
        for(ItemType w : weapons) {
            if(name.contains(w.getName())) return true;
        }
        return false;
    }
}
