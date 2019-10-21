package me.raindance.champions.kits.skills.RangerSkills;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowChargeUp;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Overcharge extends BowChargeUp {
    private double bonusDamage;

    public Overcharge(Player player, int level) {
        super(player, "Overcharge", level, SkillType.Ranger, InvType.PASSIVEA, -1, 0);
        this.rate = (0.4f + 0.1f * level) / 20f;
        int ratePercent = (int) ((rate * 20)*100);
        this.bonusDamage = 1.5d + 1.5d * level;
        setDesc(Arrays.asList(
                "Charge your bow to deal bonus damage. ",
                "",
                "Charges %%rate%%% per Second.",
                "",
                "Deals up to %%bonus%% bonus damage."
        ));
        addDescArg("rate", () ->  ratePercent);
        addDescArg("bonus", () -> bonusDamage);
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
