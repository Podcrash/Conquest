package me.raindance.champions.kits.skills.hunter;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@SkillMetadata(id = 407, skillType = SkillType.Hunter, invType = InvType.BOW)
public class WolfFang extends BowShotSkill {

    public WolfFang() {
        super();
    }

    @Override
    public String getName() {
        return "Wolf Fang";
    }

    @Override
    public float getCooldown() {
        return 8;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        WrapperPlayServerWorldParticles bubbles = ParticleGenerator.createParticle(EnumWrappers.Particle.WATER_BUBBLE, 2);
        WrapperPlayServerWorldParticles redstone = ParticleGenerator.createParticle(EnumWrappers.Particle.REDSTONE, 2);
        ParticleGenerator.generateProjectile(arrow, bubbles);
        ParticleGenerator.generateProjectile(arrow, redstone);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        getPlayer().sendMessage(String.format("You shot %s", victim.getName()));
        StatusApplier.getOrNew(victim).applyStatus(Status.BLEED, 6, 1);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
    }
}
