package me.raindance.champions.kits.skills.AssassinSkills;

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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class SilencingArrow extends BowShotSkill {
    private final int MAX_LEVEL = 4;
    private final int duration;

    public SilencingArrow(Player player, int level) {
        super(player, "Silencing Arrow", level, SkillType.Assassin, ItemType.BOW, InvType.BOW, 20 - 3 * level, false);
        duration = 3;
        setDesc("Left Click to prepare.",
                "Your next arrow deals Silence for %%duration%% seconds, which prevents skills from being used.");
        addDescArg("duration", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Player player = getPlayer();
        player.sendMessage(getUsedMessage());
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().clone(), EnumWrappers.Particle.SPELL, 1,
                0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        StatusApplier.getOrNew(victim).applyStatus(Status.SILENCE, duration, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BAT_HURT, 1.0f, 1.5f);
        event.addSkillCause(this);
        //shooter.sendMessage(String.format("Skill> You silenced %s for %d seconds.", victim.getName(), duration));
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
    }
}
