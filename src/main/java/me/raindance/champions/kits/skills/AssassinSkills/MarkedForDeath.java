package me.raindance.champions.kits.skills.AssassinSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class MarkedForDeath extends BowShotSkill {
    private final int MAX_LEVEL = 4;
    private final float duration;
    private Map<String, Long> victims;
    private final double heal; // will be used later

    public MarkedForDeath(Player player, int level) {
        super(player, "Marked for Death", level, SkillType.Assassin, ItemType.BOW, InvType.BOW, 20 - 2 * level, false);
        this.victims = new HashMap<>();
        duration = 2.5F + level * 1.5F;
        heal = 1 + 0.5 * level;
        setDesc("Left Click to prepare.",
                "Your next arrow marks the player for %%duration%% seconds,",
                "So they take 10% more damage.",
                "Players dying under your mark heal you for %%heal%% health");
        addDescArg("duration", () -> duration);
        addDescArg("heal", () -> heal);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Player player = getPlayer();
        player.sendMessage(getUsedMessage());
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().toVector(), EnumWrappers.Particle.SPELL_MOB, 1,
                0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        if(event.isCancelled()) return;
        StatusApplier.getOrNew(victim).applyStatus(Status.MARKED, duration, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BLAZE_BREATH, 1.0f, 1.5f);
        event.addSource(this);
        victims.put(victim.getName(), System.currentTimeMillis() + 1000L * (long) duration);
        //shooter.sendMessage("You marked " + victim.getName() + " for " + duration + " seconds.");
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        return;
    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        LivingEntity entity = event.getVictim();
        if(victims.containsKey(entity.getName()) && System.currentTimeMillis() <= victims.get(entity.getName())) {
            event.addSource(this);
            event.setModified(true);
            event.setDamage(event.getDamage() * 1.1D);
        }
    }

    @EventHandler
    public void death(DeathApplyEvent event) {
        if(victims.containsKey(event.getPlayer().getName())) {
            long time = victims.get(event.getPlayer().getName());
            victims.remove(event.getPlayer().getName());
            if (System.currentTimeMillis() < time) {
                getChampionsPlayer().heal(heal);
                getPlayer().sendMessage("MarkedForDeath> You healed " + heal + " HP!");
            }
        }
    }
}
