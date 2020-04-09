package me.raindance.champions.events.skill;

import me.raindance.champions.kits.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public class SkillInteractEvent extends SkillUseEvent {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity interacted;
    public SkillInteractEvent(Skill skill, LivingEntity interacted) {

        super(skill);
        this.interacted = interacted;
    }

    public LivingEntity getInteracted() {
        return interacted;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
