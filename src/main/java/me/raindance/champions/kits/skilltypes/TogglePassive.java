package me.raindance.champions.kits.skilltypes;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * TogglePassive is a type of passive! It is not new, just makes it easier to code.
 */
public abstract class TogglePassive extends Drop {
    private boolean toggled;
    public TogglePassive() {
        super();
        this.toggled = false;
    }

    public boolean drop(PlayerDropItemEvent event) {
        forceToggle(); //is this right?
        toggle();
        return true;
    }

    protected void forceToggle() {
        toggled = !toggled;
    }

    public boolean isToggled() {
        return toggled;
    }

    public abstract void toggle();


    protected String getToggleMessage() {
        //the reason why this is inverted is because the message sent is before the toggle method is called
        //meaning, it will be inverted before it's called, but to the user it's confusing
        //so we invert it
        String status = (isToggled()) ? ChatColor.GOLD + "Enabled" : ChatColor.RED + "Disabled";
        return String.format(ChatColor.BLUE + "Skill> " + ChatColor.RESET + ChatColor.GREEN + "%s: %s", getName(), status);
    }

    @Override
    public String getUsedMessage() {
        return getToggleMessage();
    }
}
