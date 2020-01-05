package me.raindance.champions.kits.skills.marksman;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowChargeUp;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Arrays;

@SkillMetadata(id = 508, skillType = SkillType.Marksman, invType = InvType.PASSIVEA)
public class Overcharge extends BowChargeUp {
    private final double bonusDamage;
    private final float rate;

    public Overcharge() {
        this.rate = (0.4f + 0.1f * 2) / 20f;
        this.bonusDamage = 5;
    }

    @Override
    public float getRate() {
        return rate;
    }

    @Override
    public String getName() {
        return "Overcharge";
    }

    @Override
    public void doShoot(Arrow arrow, float charge) {
        ParticleGenerator.generateProjectile(arrow, ParticleGenerator.createParticle(arrow.getLocation().toVector(), EnumWrappers.Particle.REDSTONE, 10, 0, 0, 0));
    }

    @Override
    public void shootPlayer(Arrow arrow, float charge, DamageApplyEvent e) {
        double bonus = bonusDamage * charge;
        e.setModified(true);
        e.setDamage(e.getDamage() + bonus);
        getPlayer().sendMessage(String.format("Skill> You shot %s for %.2f more damage with %s", e.getVictim().getName(), bonus, getName()));
        e.addSource(this);
    }

    @Override
    public void shootGround(Arrow arrow, float charge) {

    }

    public int getMaxLevel() {
        return 3;
    }
}
