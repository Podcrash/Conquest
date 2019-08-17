package me.raindance.champions.events;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.kits.Skill;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DamageApplyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private boolean modified;

    private LivingEntity victim;
    private LivingEntity attacker;

    private double damage;

    private Cause cause;
    private Arrow arrow;
    private List<Skill> skills;

    private boolean doKnockback;
    private double[] velocityModifier;

    public DamageApplyEvent(LivingEntity victim, LivingEntity attacker, double damage, Cause cause, Arrow arrow, List<Skill> skills, boolean doKnockback) {
        super(true);
        this.modified = false;
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.cause = cause;
        this.arrow = arrow;
        this.skills = skills; //ignore, it's not a singleton list
        this.doKnockback = doKnockback;
        this.velocityModifier = new double[]{1, 1, 1};
    }

    public DamageApplyEvent(LivingEntity victim, LivingEntity attacker, double damage, Cause cause, Arrow arrow, Skill skill, boolean doKnockback) {
        this(victim, attacker, damage, cause, arrow, new ArrayList<>(Collections.singletonList(skill)), doKnockback);
    }



    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public boolean isModified() {
        return modified;
    }
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public LivingEntity getVictim() {
        return victim;
    }
    public LivingEntity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Cause getCause() {
        return cause;
    }
    public Arrow getArrow() {
        return arrow;
    }
    public List<Skill> getSkill() {
        return skills;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }
    public void addSkillCause(Skill skill) {
        skills.add(skill);
    }

    public boolean isDoKnockback() {
        return doKnockback;
    }
    public void setDoKnockback(boolean knockback) {
        this.doKnockback = knockback;
    }

    public double[] getVelocityModifiers() {
        return velocityModifier;
    }
    public double getVelocityModifierX() {
        return velocityModifier[0];
    }
    public double getVelocityModifierY() {
        return velocityModifier[1];
    }
    public double getVelocityModifierZ() {
        return velocityModifier[2];
    }

    public void setVelocityModifierX(double value) {
        this.velocityModifier[0] = value;
    }
    public void setVelocityModifierY(double value) {
        this.velocityModifier[1] = value;
    }
    public void setVelocityModifierZ(double value) {
        this.velocityModifier[2] = value;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

