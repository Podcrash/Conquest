package me.raindance.champions.kits.skills.RangerSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import me.raindance.champions.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IncendiaryShot extends BowShotSkill {
    private WrapperPlayServerWorldParticles fire;
    private float scaling;
    public IncendiaryShot(Player player, int level) {
        super(player, "Incendiary Shot", level,  SkillType.Ranger, ItemType.BOW, InvType.BOW, 10F + 1.5F * level, false);
        fire = ParticleGenerator.createParticle(null, EnumWrappers.Particle.LAVA, 2, 0,0,0);
        this.scaling = 8 - level;
        setDesc(Arrays.asList(
                "Prepare an Incendiary Shot: ",
                "",
                "Your next arrow will pack a fiery",
                "punch, setting your target aflame. ",
                "",
                "The greater the distance between you and",
                "your opponent, the longer they will burn. "
        ));
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        ParticleGenerator.generateProjectile(arrow, fire);
        arrow.setFireTicks(1000);
        SoundPlayer.sendSound(getPlayer().getLocation(), "fireworks.blast", 1F, 65);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        victim.setFireTicks(0);
        float duration = level/2F + (float) victim.getLocation().distance(shooter.getLocation())/scaling;
        event.addSkillCause(this);
        getPlayer().sendMessage("IncendiaryShot> You burned " + victim.getName() + " for " + duration + " seconds.");
        StatusApplier.getOrNew(victim).applyStatus(Status.FIRE, duration, 1, false);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {

    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
