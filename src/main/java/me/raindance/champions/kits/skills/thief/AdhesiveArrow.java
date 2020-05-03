package me.raindance.champions.kits.skills.thief;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
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
        return 7;
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
    protected void shotEntity(DamageApplyEvent event, Player shooter, LivingEntity victim, Arrow arrow, float force) {
        StatusApplier.getOrNew(victim).applyStatus(Status.GROUND, 5F, 1);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
    }
}
