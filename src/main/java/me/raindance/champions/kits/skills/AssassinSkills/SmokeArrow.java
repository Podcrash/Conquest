package me.raindance.champions.kits.skills.AssassinSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class SmokeArrow extends BowShotSkill {
    private final int MAX_LEVEL = 4;
    private final int duration;

    public SmokeArrow(Player player, int level) {
        super(player, "Smoke Arrow", level, SkillType.Assassin, ItemType.BOW, InvType.BOW, 20 - 2 * level, false);
        duration = 3 + level;
        setDesc("Left Click to prepare.",
                "Your next arrow deals Blindness and Slowness II for %%duration%% seconds.");
        addDescArg("duration", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Player player = getPlayer();
        player.sendMessage(getUsedMessage());
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().clone(), EnumWrappers.Particle.SMOKE_LARGE, 1,
                0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        StatusApplier.getOrNew(victim).applyStatus(Status.BLIND, duration, 1);
        StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, duration, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BLAZE_BREATH, 1.0f, 1.5f);
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(victim.getLocation(), EnumWrappers.Particle.EXPLOSION_NORMAL, 1, 0, 0, 0);
        event.addSkillCause(this);
        for(Player player : getPlayers()) packet.sendPacket(player);
        shooter.sendMessage(String.format("You smoked %s for %d seconds.", victim.getName(), duration));
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        return;
    }

}
