package me.raindance.champions.kits.skills.thief;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

@SkillMetadata(id = 702, skillType = SkillType.Thief, invType = InvType.BOW)
public class BlindingArrow extends BowShotSkill {
    private final int duration = 4;

    @Override
    public float getCooldown() {
        return 16;
    }

    @Override
    public String getName() {
        return "Blinding Arrow";
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
        event.addSource(this);
        PacketUtil.syncSend(packet, getPlayers());
        shooter.sendMessage(String.format("You smoked %s for %d seconds.", victim.getName(), duration));
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        return;
    }

}
