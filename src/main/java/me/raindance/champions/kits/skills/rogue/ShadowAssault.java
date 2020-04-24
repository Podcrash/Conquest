package me.raindance.champions.kits.skills.rogue;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;


/**
 * SHADOW ASSAULT REWORK:
 * “After sneaking for 2 seconds, gain True Invisibility, also your next melee attack while hidden will deal 2 bonus damage.
 * Shadow Assault cancels/ends if you deal or take damage.”
 */
@SkillMetadata(id = 608, skillType = SkillType.Rogue, invType = InvType.SECONDARY_PASSIVE)
public class ShadowAssault extends Passive implements IPassiveTimer, TimeResource {

    private boolean isReady = false;
    private long started = -1;

    private double chargeTime = 2.0;
    private double bonusDamage = 2;

    @Override
    public String getName() {
        return "Shadow Assault";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(DamageApplyEvent event) {
        if (event.getAttacker().equals(getPlayer())) {
            if(isReady) {
                event.setDamage(event.getDamage() + bonusDamage);
                event.addSource(this);
                event.setModified(true);

                SoundPlayer.sendSound(getPlayer().getLocation(), "mob.zombie.metal", 1, 70);
                WrapperPlayServerWorldParticles startEffect = ParticleGenerator.createParticle(
                        getPlayer().getEyeLocation().toVector(), EnumWrappers.Particle.EXPLOSION_NORMAL, 1, 0, 0, 0);
                PacketUtil.syncSend(startEffect, GameManager.getGame().getBukkitPlayers());
                getPlayer().sendMessage(getUsedMessage());
            }
            Bukkit.getScheduler().runTask(Main.instance, this::reset);
        }
        if(event.getVictim().equals(getPlayer())) {
            if (getPlayer().isSneaking()) {
                WrapperPlayServerWorldParticles startEffect = ParticleGenerator.createParticle(
                        getPlayer().getEyeLocation().toVector(), EnumWrappers.Particle.EXPLOSION_NORMAL, 5, 0, 0, 0);
                PacketUtil.syncSend(startEffect, GameManager.getGame().getBukkitPlayers());
            }
            Bukkit.getScheduler().runTask(Main.instance, this::reset);
        }

    }

    @Override
    public void start() {
        run(1, 0);
    }

    @Override
    public void stop() {
        reset();
    }

    @Override
    public void task() {
        if (getPlayer().isSneaking() && !isReady) {
            if (started == -1) {
                started = System.currentTimeMillis();
            }
            if(System.currentTimeMillis() - started > (chargeTime * 1000)) {
                isReady = true;
                SoundPlayer.sendSound(getPlayer().getLocation(), "mob.enderdragon.wings", 1, 63);
                WrapperPlayServerWorldParticles startEffect = ParticleGenerator.createParticle(
                        getPlayer().getEyeLocation().toVector(), EnumWrappers.Particle.EXPLOSION_NORMAL, 5, 0, 0, 0);
                PacketUtil.syncSend(startEffect, GameManager.getGame().getBukkitPlayers());
                StatusApplier.getOrNew(getPlayer()).applyStatus(Status.CLOAK, 20000, 1);
            }
        } else if (!getPlayer().isSneaking()) {
            reset();
        }
    }

    @EventHandler
    public void onSkillUse(SkillUseEvent event) {
        if(event.getPlayer().equals(getPlayer()))
            reset();
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() { }

    private void reset() {
        started = -1;
        isReady = false;
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.CLOAK);
    }
}
