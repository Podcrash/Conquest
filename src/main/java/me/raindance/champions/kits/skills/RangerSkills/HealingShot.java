package me.raindance.champions.kits.skills.RangerSkills;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerEntityStatus;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
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
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(victim.getLocation().toVector(), EnumWrappers.Particle.HEART,
                3, rand.nextFloat(), -0.9f, rand.nextFloat());
        WrapperPlayServerEntityStatus status = new WrapperPlayServerEntityStatus();
        status.setEntityId(victim.getEntityId());
        status.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);
        PacketUtil.syncSend(new AbstractPacket[]{status, packet}, getPlayers());
        SoundPlayer.sendSound(victim.getLocation(), "random.levelup", 0.9F, 95);
        event.addSource(this);
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
