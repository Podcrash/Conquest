package me.raindance.champions.damage;

import me.raindance.champions.kits.Skill;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Damage {
    private final LivingEntity victim;
    private final LivingEntity attacker;

    private final double damage;

    private final ItemStack item;
    private final Cause damageCause;
    private final Arrow arrow;
    private final List<Skill> skills;

    private final boolean applyKnockback;

    private long time;

    public Damage(LivingEntity victim, LivingEntity attacker, double damage, ItemStack itemStack, Cause damageCause, Arrow arrow, List<Skill> skills, boolean applyKnockback) {
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.item = itemStack;
        this.damageCause = damageCause;
        this.arrow = arrow;
        this.skills = skills;
        this.applyKnockback = applyKnockback;
        this.time = System.currentTimeMillis();
    }

    public Damage(LivingEntity victim, LivingEntity attacker, double damage, ItemStack item, Cause damageCause, Arrow arrow, Skill skill, boolean applyKnockback) {
        this(victim, attacker, damage, item, damageCause, arrow, new ArrayList<>(Collections.singletonList(skill)), applyKnockback);
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

    public ItemStack getItem() {
        return item;
    }
    public Cause getCause() {
        return damageCause;
    }
    public Arrow getArrow() {
        return arrow;
    }
    public List<Skill> getSkills() {
        return skills;
    }

    public boolean isApplyKnockback() {
        return applyKnockback;
    }

    public long getTime() {
        return time;
    }
}
