package me.raindance.champions.events.skill;

import me.raindance.champions.kits.Skill;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class SkillUseEvent extends SkillEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public SkillUseEvent(Skill skill) {
        super(skill);
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
