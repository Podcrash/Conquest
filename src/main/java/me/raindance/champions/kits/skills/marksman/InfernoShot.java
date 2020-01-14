package me.raindance.champions.kits.skills.marksman;

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
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Arrays;

@SkillMetadata(id = 505, skillType = SkillType.Marksman, invType = InvType.BOW)
public class InfernoShot extends BowShotSkill implements IConstruct {
    private WrapperPlayServerWorldParticles fire;
    private final float scaling = 6;

    @Override
    public void afterConstruction() {
        this.fire = ParticleGenerator.createParticle(null, EnumWrappers.Particle.LAVA, 2, 0,0,0);
    }

    @Override
    public float getCooldown() {
        return 9;
    }

    @Override
    public String getName() {
        return "Inferno Shot";
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        ParticleGenerator.generateProjectile(arrow, fire);
        arrow.setFireTicks(1000);
        SoundPlayer.sendSound(getPlayer().getLocation(), "fireworks.blast", 1F, 65);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        if(isAlly(victim)) return;
        victim.setFireTicks(0);
        //float duration = 1.5F + (float) victim.getLocation().distance(shooter.getLocation())/scaling;
        float duration = 5.5F;
        event.addSource(this);
        getPlayer().sendMessage("IncendiaryShot> You burned " + victim.getName() + " for " + duration + " seconds.");
        StatusApplier.getOrNew(victim).applyStatus(Status.FIRE, duration, 1, false);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {

    }
}
