package me.raindance.champions.kits.skills.BruteSkills;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.resources.EntityParticleResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class Bloodlust extends Passive {
    private int duration;
    private PotionEffect strength, speed, regen;
    private Bloodlust instance;
    private BloodlustParticleResource resource;
    private long current;
    public Bloodlust(Player player, int level) {
        super(player, "Bloodlust", level, SkillType.Brute, InvType.PASSIVEA);
        this.instance = this;
        this.duration = this.level + 6;
        this.strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, 0);
        if(player != null) resource = new BloodlustParticleResource();
        setDesc(Arrays.asList(
                "Killing an enemy will cause you ",
                "to go into a Bloodlust, ",
                "receiving Speed 1 and",
                "Strength 1 for %%duration%% seconds.",
                "You also gain regeneration ",
                "for %%duration%% seconds.",
                "",
                "Bloodlust can stack up to 3 times, ",
                "boosting the level of Speed and ",
                "Regen by 1."
        ));
        addDescArg("duration", () ->  duration);
    }

    private class BloodlustParticleResource extends EntityParticleResource {
        public BloodlustParticleResource() {
            super(instance.getPlayer(), ParticleGenerator.createParticle(EnumWrappers.Particle.REDSTONE, 2), null);
        }

        @Override
        public boolean cancel() {
            return System.currentTimeMillis() - getLastUsed() >= duration * 1000L || getPlayer().isDead();
        }
    }
    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void kill(DeathApplyEvent e) {
        //check if the player who died isn't nothing
        //check if the player who killed was this player, and if this player was not the victim as well (/kill)
        //If this player isn't dead to prevent bs ressurection tactics
        if (e.getPlayer() != null && (e.getAttacker() == getPlayer() && e.getPlayer() != getPlayer())  && !getPlayer().isDead()) {
            int potencySpeed = -1;
            for (PotionEffect potion : getPlayer().getActivePotionEffects()) {
                if (potion.getType().equals(PotionEffectType.SPEED)) {
                    potencySpeed = potion.getAmplifier();
                }
            }

            int speedLevel = potencySpeed + 1;
            if(speedLevel > getMaxLevel() - 1) speedLevel = getMaxLevel() - 1;
            this.speed = new PotionEffect(PotionEffectType.SPEED, duration * 20, speedLevel);
            this.regen = new PotionEffect(PotionEffectType.REGENERATION, duration * 20, speedLevel);
            getChampionsPlayer().heal(0.5D);
            Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
                boolean a = getPlayer().addPotionEffect(strength, true);
                boolean b = getPlayer().addPotionEffect(speed, true);
                boolean c = getPlayer().addPotionEffect(regen, true);
                if(a && b && c) {
                    getPlayer().sendMessage(String.format("%sBrute> %sYou gained Bloodlust.", ChatColor.BLUE, ChatColor.GRAY));
                    current = System.currentTimeMillis();
                }
            }, 1L);
            SoundPlayer.sendSound(getPlayer().getLocation(), "mob.zombiepig.zpigangry", 1f, 110);
            this.setLastUsed(System.currentTimeMillis());
            resource.runAsync(1,1);
        }
    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if(event.getAttacker() == getPlayer() && (event.getCause() == Cause.MELEE || event.getCause() == Cause.MELEESKILL)) {
            if(System.currentTimeMillis() - current <= duration * 1000L) event.addSource(this);
        }
    }
}
