package me.raindance.champions.kits.skills.marksman;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowChargeUp;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Arrays;

@SkillMetadata(id = 508, skillType = SkillType.Marksman, invType = InvType.PASSIVEA)
public class Overcharge extends BowChargeUp {
    private final double bonusDamage;
    private final float rate;

    public Overcharge() {
        this.rate = (1.2f) / 20f;
        this.bonusDamage = 8;
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
        getPlayer().sendMessage(String.format("%sSkill> %sYou shot %s%s %sfor %s%.2f more damage with %s%s%s.",
                ChatColor.BLUE, ChatColor.GRAY, ChatColor.YELLOW, e.getVictim().getName(), ChatColor.GRAY, bonus, ChatColor.GREEN, getName(), ChatColor.GRAY));
        e.addSource(this);
    }

    @Override
    public void shootGround(Arrow arrow, float charge) {

    }
}
