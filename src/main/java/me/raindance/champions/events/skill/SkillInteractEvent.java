package me.raindance.champions.events.skill;

import me.raindance.champions.kits.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

public class SkillInteractEvent extends SkillUseEvent {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity interactor;
    public SkillInteractEvent(Skill skill, LivingEntity interactor) {
        super(skill);
        this.interactor = interactor;
    }

    public LivingEntity getInteractor() {
        return interactor;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
