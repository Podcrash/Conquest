package me.raindance.champions.kits.skills.AssassinSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Location;
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
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().toVector(), EnumWrappers.Particle.SMOKE_LARGE, 1,
                0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        if(event.isCancelled()) return;
        StatusApplier.getOrNew(victim).applyStatus(Status.BLIND, duration, 1);
        StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, duration, 1);
        SoundPlayer.sendSound(victim.getLocation(), "mob.blaze.breathe", 1, 100);
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(victim.getLocation().toVector(), EnumWrappers.Particle.EXPLOSION_LARGE, 1, 0, 0, 0);
        event.addSkillCause(this);
        PacketUtil.syncSend(packet, getPlayers());
        shooter.sendMessage(String.format("You smoked %s for %d seconds.", victim.getName(), duration));
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        return;
    }

}
