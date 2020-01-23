package me.raindance.champions.kits.skills.hunter;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Continuous;
import org.bukkit.event.EventHandler;

import java.util.Random;

@SkillMetadata(id = 409, skillType = SkillType.Marksman, invType = InvType.SWORD)
public class Rest extends Continuous {
    private boolean active;
    private Random rand;
    @Override
    protected void doContinuousSkill() {
        rand = new Random();
        active = true;
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.REGENERATION, Integer.MAX_VALUE, 0);
        startContinuousAction();
    }

    @Override
    public void task() {
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(), EnumWrappers.Particle.HEART,
                3, rand.nextFloat(), 0.9f, rand.nextFloat());
        getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.REGENERATION);
    }

    @Override
    public String getName() {
        return "Rest";
    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(!active || event.getVictim() == getPlayer()) return;
        event.setModified(true);
        event.addSource(this);
        event.setDamage(event.getDamage() + 1);
    }
}
