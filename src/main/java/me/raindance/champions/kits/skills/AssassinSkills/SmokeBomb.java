package me.raindance.champions.kits.skills.AssassinSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IDropPassive;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.SimpleTimeResource;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SmokeBomb extends Passive implements IDropPassive {
    private final int MAX_LEVEL = 3;
    private final int duration;
    private final int distance;
    private final int distanceSquared;
    private final int blindDuration;
    private boolean isInvis;

    public SmokeBomb(Player player, int level) {
        super(player, "Smoke Bomb", level, SkillType.Assassin, InvType.PASSIVEA, 60 - 10 * level);
        duration = level * 2 + 1;
        this.distance = 2 + level;
        this.distanceSquared = distance * distance;
        this.blindDuration = 1 + level;
        setDesc("Create a smoke explosion around you, turning you invisible,",
                "for %%duration%% seconds and Blinding enemy players",
                "within %%distance%% blocks for %%blindDuration%% seconds.");
        addDescArg("duration", () -> duration);
        addDescArg("distance", () -> distance);
        addDescArg("blindDuration", () -> blindDuration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void drop(PlayerDropItemEvent e) {
        if (checkItem(e.getItemDrop().getItemStack()) && !isInWater() && e.getPlayer() == getPlayer()) {
            doSkill();
            e.setCancelled(true);
        }
    }

    private final SmokeBombTrail trail = new SmokeBombTrail();
    public void doSkill() {
        if (!onCooldown()) {
            SkillUseEvent useEvent = new SkillUseEvent(this);
            Bukkit.getPluginManager().callEvent(useEvent);
            if (useEvent.isCancelled()) return;
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
            Location loc = getPlayer().getLocation();
            List<Player> players = loc.getWorld().getPlayers();
            players.forEach((player) -> {
                if (player != this.getPlayer()) {
                    Location loc2 = player.getLocation();
                    double distanceS = loc2.distanceSquared(loc);
                    if (distanceS < distanceSquared) {
                        float duration = (isAlly(player)) ? blindDuration/2 : blindDuration;
                        StatusApplier.getOrNew(player).applyStatus(Status.BLIND, duration, 1);
                    }

                }
            });
            this.setLastUsed(System.currentTimeMillis());
            this.getPlayer().sendMessage(getUsedMessage());
        } else this.getPlayer().sendMessage(getCooldownMessage());
    }

    /*
    This must be changed to GameDamageEvent later
     */
    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void interact(PlayerInteractEvent event) {
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        Player player = event.getPlayer();
        if (isInvis && player == getPlayer() && applier.isCloaked()) {
            applier.removeCloak();
            isInvis = false;
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        if (isInvis && applier.isCloaked() && event.getCause() != Cause.MELEE) {
            LivingEntity victim = event.getVictim();
            LivingEntity damager = event.getAttacker();
            if (victim == getPlayer() || damager == getPlayer()) {
                applier.removeCloak();
                isInvis = false;
            }
        }
    }

    private class SmokeBombTrail implements TimeResource {
        private final WrapperPlayServerWorldParticles smokeTrail = ParticleGenerator.createParticle(EnumWrappers.Particle.SMOKE_NORMAL, 2);
        @Override
        public void task() {
            smokeTrail.setLocation(getPlayer().getLocation());
            PacketUtil.syncSend(smokeTrail, getPlayers());
        }

        @Override
        public boolean cancel() {
            return !isInvis;
        }

        @Override
        public void cleanup() {

        }
    }
}
