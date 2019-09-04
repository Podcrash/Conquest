package me.raindance.champions.kits.skills.RangerSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.callback.sources.AfterHit;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;
import java.util.Random;

public class VitalitySpores extends Passive {

    private final int MAX_LEVEL = 3;
    private final Random rand = new Random();
    private int effectTime;
    private boolean active = false;
    private AfterHit afterHit;

    public VitalitySpores(Player player, int level) {
        super(player, "Vitality Spores", level, SkillType.Ranger, InvType.PASSIVEA, 8);
        this.effectTime = 5 + level;
        this.afterHit = new AfterHit(10, 1, 8000).then(() -> {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.REGENERATION, effectTime, 1);
            active = true;
            TimeHandler.delayTime(effectTime * 20, new TimeResource() {
                @Override
                public void task() {

                }

                @Override
                public boolean cancel() {
                    return true;
                }

                @Override
                public void cleanup() {
                    active = false;
                }
            }); //this makes soups work if you are hit
            WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(), EnumWrappers.Particle.HEART,
                    3, rand.nextFloat(), 0.9f, rand.nextFloat());
            getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
        });
        setDesc(Arrays.asList(
                "After getting hit, if no damage is taken ",
                "for 8 seconds then you will receive ",
                "Regeneration 2 for %%duration%% seconds"
        ));
        addDescArg("duration", () ->  effectTime);
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if (event.getVictim() == getPlayer()) {
            add();
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(EntityDamageEvent event) {
        if(event.isCancelled()) return;
        if (event.getEntity() == getPlayer() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            add();
        }
    }

    private void add() {
        if (active) {
            StatusApplier.getOrNew(getPlayer()).removeVanilla(Status.REGENERATION);
            active = false;
        }
        afterHit.run();
    }
}
