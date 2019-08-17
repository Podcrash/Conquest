package me.raindance.champions.kits.skills.BruteSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.sound.SoundWrapper;
import me.raindance.champions.time.resources.EntityParticleResource;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.Arrays;

public class Stampede extends Passive implements IPassiveTimer, ICharge {
    private int charges = 0;
    private long time;
    private final int MAX_LEVEL = 3;
    private boolean toggle;
    private int timing;
    private int currentSpeed = -1;
    
    private final StampedeParticleResource resource;
    public Stampede(Player player, int level) {
        super(player, "Stampede", level,  SkillType.Brute, InvType.PASSIVEA);
        charges = 0;
        this.toggle = false;
        this.timing = 6 - level;
        double bonus = (0.25 + level * 0.25);
        resource = player != null ? new StampedeParticleResource(getPlayer(), ParticleGenerator.createParticle(null, EnumWrappers.Particle.CRIT, 2, 0,0,0), null) : null;
        setDesc(Arrays.asList(
                "You slowly build up speed as you ",
                "sprint. You gain a level of Speed ",
                "every %%time%% seconds, up to a max of",
                "Speed 2.",
                "",
                "Attacking during stampede deals ",
                "%%damage%% bonus damage, ",
                "and +50% knockback per Speed level.",
                "",
                "Resets if you take damage."
        ));
        addDescArg("time", () ->  timing);
        addDescArg("damage", () -> bonus);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public void start() {
        time = System.currentTimeMillis();
        run(1, 1);
        resource.run(1,1);
    }

    @Override
    public void addCharge() {
        charges = charges < MAX_LEVEL ? charges + 1 : MAX_LEVEL;
    }

    @Override
    public int getCurrentCharges() {
        return charges;
    }

    @Override
    public int getMaxCharges() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
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
            if(currentSpeed >= 0) StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, 1, currentSpeed, false);
            if (currentSpeed != 1 && System.currentTimeMillis() - time >= 1000L * timing) {
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

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent event) {
        if (event.getAttacker() == getPlayer()) {
            if (event.getCause() != Cause.MELEE) return;
            if(charges == 0) return;
            event.setModified(true);
            event.addSkillCause(this);
            double bonus = event.getDamage() + ((0.25 + level * 0.25) * charges);
            event.setVelocityModifierX(1 + charges * 0.5);
            event.setVelocityModifierZ(1 + charges * 0.5);
            getPlayer().getWorld().playSound(event.getVictim().getLocation(), Sound.ZOMBIE_WOOD, 0.5f, 1);
            reset();
            check(getPlayer().isSprinting());
            event.setDamage(bonus);
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
