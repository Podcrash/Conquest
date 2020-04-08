package me.raindance.champions.events.skill;

import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// This event is called when a skill comes off cooldown.
public class SkillRechargeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ICooldown ICooldown;

    public SkillRechargeEvent(ICooldown skill) {
        ICooldown = skill;
    }

    public ICooldown getICooldown() {return ICooldown;}

    public String getSkillName() {return ICooldown.getName();}

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
