package me.raindance.champions.kits.skills.thief;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.SimpleTimeResource;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@SkillMetadata(id = 710, skillType = SkillType.Thief, invType = InvType.DROP)
public class Stealth extends Drop implements ICooldown, IConstruct {
    private final int duration = 8;
    private SmokeBombTrail trail;
    private boolean isInvis;

    @Override
    public void afterConstruction() {
        trail = new SmokeBombTrail();
    }

    @Override
    public float getCooldown() {
        return 20;
    }

    @Override
    public String getName() {
        return "Stealth";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    public boolean drop(PlayerDropItemEvent e) {
        if (onCooldown()) {
            this.getPlayer().sendMessage(getCooldownMessage());
            return false;
        }
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        applier.applyStatus(Status.CLOAK, duration, 1);
        isInvis = true;
        TimeHandler.delayTime(duration * 20L, new SimpleTimeResource() {
            @Override
            public void task() {
                isInvis = false;
            }
        });
        TimeHandler.repeatedTime(20, 0, trail);
        getPlayer().getWorld().playEffect(getPlayer().getLocation(), Effect.EXPLOSION_HUGE, 1);
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIZZ, 2f, 0.5f);
        this.setLastUsed(System.currentTimeMillis());
        return true;
    }

    /*
    This must be changed to GameDamageEvent later
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        if(event.getPlayer() == getPlayer())
            cancelInvis(getPlayer());
    }

    @EventHandler
    public void interact(PlayerInteractAtEntityEvent event) {
        if(event.getPlayer() == getPlayer())
            cancelInvis(getPlayer());
    }

    public void cancelInvis(Player player) {
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        if (isInvis && applier.isCloaked()) {
            applier.removeCloak();
        }
        isInvis = false;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if(event.getVictim() == getPlayer() || event.getAttacker() == getPlayer())
            cancelInvis(getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamaged(EntityDamageEvent event) {
        if (event.getEntity() == getPlayer()) {
            cancelInvis(getPlayer());
        }
    }

    private class SmokeBombTrail implements TimeResource {
        private final WrapperPlayServerWorldParticles smokeTrail = ParticleGenerator.createParticle(EnumWrappers.Particle.SMOKE_LARGE, 2);
        private final StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        @Override
        public void task() {
            smokeTrail.setLocation(getPlayer().getLocation());
            PacketUtil.syncSend(smokeTrail, getPlayers());
        }

        @Override
        public boolean cancel() {
            return !applier.isCloaked();
        }

        @Override
        public void cleanup() {

        }
    }
}
