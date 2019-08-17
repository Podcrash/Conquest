package me.raindance.champions.kits.skills.RangerSkills;

import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
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
import java.util.Random;

public class HealingShot extends BowShotSkill {
    private int duration;
    private final WrapperPlayServerWorldParticles particle;
    public HealingShot(Player player, int level) {
        super(player, "Healing Shot", level, SkillType.Ranger, ItemType.BOW, InvType.BOW, 20 - 3 * level, false);
        this.duration = 3 + level;
        this.particle = ParticleGenerator.createParticle(null, EnumWrappers.Particle.HEART, 2, 0,0,0);
        setDesc(Arrays.asList(
                "Prepare a Healing Shot: ",
                "",
                "Your next arrow will give its target ",
                "Regeneration 3 for %%duration%% seconds, ",
                "and remove all negative effects. ",
                "",
                "Self hits give Regeneration 2.",
                "",
                "Gives Nausea to enemies for %%duration2%% seconds."
        ));
        addDescArg("duration", () ->  duration);
        addDescArg("duration2", () -> duration + 2);
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        ParticleGenerator.generateProjectile(arrow, this.particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        final Random rand = new Random();
        boolean ally = victim == getPlayer() || isAlly(victim);
        if(ally) {
            event.setModified(true);
            event.setDamage(0);
            event.setDoKnockback(false);
        }
        if(victim == getPlayer()) {
            clearBad(StatusApplier.getOrNew(getPlayer())).applyStatus(Status.REGENERATION, duration, 1, true);
        }else if(isAlly(victim)){
            clearBad(StatusApplier.getOrNew(victim)).applyStatus(Status.REGENERATION, duration, 2, true);
        }else {
            StatusApplier.getOrNew(victim).applyStatus(Status.DIZZY, duration + 2, 1, true);
        }
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(victim.getLocation(), EnumWrappers.Particle.HEART,
                3, rand.nextFloat(), -0.9f, rand.nextFloat());
        WrapperPlayServerEntityStatus status = new WrapperPlayServerEntityStatus();
        status.setEntityId(victim.getEntityId());
        status.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);
        for(Player player : getPlayers()) {
            status.sendPacket(player);
            packet.sendPacket(player);
        }
        SoundPlayer.sendSound(victim.getLocation(), "random.levelup", 0.9F, 95);
        event.addSkillCause(this);
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {

    }

    private StatusApplier clearBad(StatusApplier applier){
        for(Status status : applier.getEffects()) {
            if(status.isNegative()) applier.removeStatus(status);
        }
        return applier;
    }
    @Override
    public int getMaxLevel() {
        return 4;
    }
}
