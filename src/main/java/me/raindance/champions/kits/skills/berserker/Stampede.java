package me.raindance.champions.kits.skills.berserker;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.classes.Berserker;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICharge;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.time.resources.EntityParticleResource;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.Arrays;

@SkillMetadata(id = 108, skillType = SkillType.Berserker, invType = InvType.PASSIVEA)
public class Stampede extends Passive implements IPassiveTimer, ICharge {
    private int charges = 0;
    private long time;
    private boolean toggle;
    private int timing;
    private int currentSpeed = -1;
    
    private StampedeParticleResource resource;
    public Stampede() {
        charges = 0;
        this.toggle = false;
        this.timing = 3;
    }

    @Override
    public String getName() {
        return "Stampede";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(EnumWrappers.Particle.CRIT, 2);
        resource = new StampedeParticleResource(getPlayer(), particles, null);

    }
    @Override
    public void start() {
        time = System.currentTimeMillis();
        run(1, 1);
        resource.run(1,1);
    }

    @Override
    public void addCharge() {
        charges = charges < 1 ? charges + 1 : 1;
    }

    @Override
    public int getCurrentCharges() {
        return charges;
    }

    @Override
    public int getMaxCharges() {
        return 1;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void sprint(PlayerToggleSprintEvent event) {
        if (event.getPlayer() != getPlayer()) return;
        check(event.isSprinting());
    }

    private void check(boolean isSprinting) {
        toggle = isSprinting;
        if (toggle) {
            start();
        } else {
            unregister();
            reset();
        }
    }

    private void incSpeed(){
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_IDLE, 0.5f, 1);
        currentSpeed++;
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.SPEED);
        addCharge();
        if(currentSpeed > 2) currentSpeed = 1;
    }

    private void resetSpeed(){
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.SPEED);
        currentSpeed = -1;
    }

    @Override
    public void task() {
        if (getPlayer().isSprinting()) {
            if(currentSpeed >= 0) StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, 1, 0, false);
            if (currentSpeed != 0 && System.currentTimeMillis() - time >= 1000L * timing) {
                time = System.currentTimeMillis();
                incSpeed();
            }
        } else toggle = false;
    }

    @Override
    public boolean cancel() {
        return !toggle;
    }

    @Override
    public void cleanup() {
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.SPEED);
        reset();
    }

    public void reset() {
        charges = 0;
        toggle = false;
        resetSpeed();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if (event.getAttacker() == getPlayer()) {
            if (event.getCause() != Cause.MELEE) return;
            if(charges == 0) return;
            event.setModified(true);
            event.addSource(this);
            event.setVelocityModifierX(event.getVelocityModifierX() * 2);
            event.setVelocityModifierY(event.getVelocityModifierY() * 1.25D);
            event.setVelocityModifierZ(event.getVelocityModifierZ() * 2);
            getPlayer().getWorld().playSound(event.getVictim().getLocation(), Sound.ZOMBIE_WOOD, 0.5f, 1);
            reset();
            check(getPlayer().isSprinting());
        } else if (event.getVictim() == getPlayer()) {
            reset();
            check(getPlayer().isSprinting());
        }
    }

    private class StampedeParticleResource extends EntityParticleResource {
        private StampedeParticleResource(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound) {
            super(entity, packet, sound);
        }

        @Override
        public boolean cancel() {
            return !toggle;
        }
    }
}
