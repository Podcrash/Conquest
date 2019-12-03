package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * TogglePassive is a type of passive! It is not new, just makes it easier to code.
 */
public abstract class TogglePassive extends Passive {
    private boolean toggled;
    public TogglePassive() {
        super();
        this.toggled = false;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void drop(PlayerDropItemEvent event) {
        if(event.getPlayer() != getPlayer()) return;
        String itemName = event.getItemDrop().getItemStack().getType().name();

        for(ItemType type : ItemType.values()) {
            if(itemName.toUpperCase().contains(type.getName())) break;
        }
        event.setCancelled(true);

        SkillUseEvent skillUse = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(skillUse);
        if(skillUse.isCancelled()) return;

        forceToggle(); //is this right?
        toggle();
    }

    protected void forceToggle() {
        toggled = !toggled;
        getPlayer().sendMessage(getToggleMessage());
    }

    public boolean isToggled() {
        return toggled;
    }

    public abstract void toggle();

    protected String getToggleMessage() {
        return String.format(ChatColor.BLUE + "Skill> " + ChatColor.RESET + ChatColor.GREEN + "%s: %s", getName(), (isToggled()) ? ChatColor.GOLD + "Enabled" : ChatColor.RED + "Disabled");

    }
}
