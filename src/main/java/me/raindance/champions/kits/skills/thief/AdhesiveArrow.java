package me.raindance.champions.kits.skills.thief;

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

@SkillMetadata(id = 701, skillType = SkillType.Thief, invType = InvType.BOW)
public class AdhesiveArrow extends BowShotSkill {

    public AdhesiveArrow() {
        super();
    }

    @Override
    public String getName() {
        return "Adhesive Arrow";
    }

    @Override
    public float getCooldown() {
        return 8;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Player player = getPlayer();
        player.sendMessage(getUsedMessage());
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().toVector(), EnumWrappers.Particle.SPELL_INSTANT,
                new int[]{150, 75, 0}, 1, 0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        StatusApplier.getOrNew(victim).applyStatus(Status.GROUND, 5F, 1);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
    }
}
