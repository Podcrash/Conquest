package me.raindance.champions.kits.skills.marksman;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.time.TimeHandler;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.HashSet;
import java.util.Set;

@SkillMetadata(id = 507, skillType = SkillType.Marksman, invType = InvType.BOW)
public class MarkedForDeath extends BowShotSkill {
    private int bonus = 12;
    private final Set<String> victims;
    public MarkedForDeath() {
        victims = new HashSet<>();
    }

    @Override
    public float getCooldown() {
        return 12;
    }

    @Override
    public String getName() {
        return "Marked For Death";
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Player player = getPlayer();
        player.sendMessage(getUsedMessage());
        WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(arrow.getLocation().toVector(), EnumWrappers.Particle.SPELL_MOB, 1,
                0,0,0);
        ParticleGenerator.generateProjectile(arrow, particle);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        if(event.isCancelled()) return;
        StatusApplier.getOrNew(victim).applyStatus(Status.MARKED, 3, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BLAZE_BREATH, 1.0f, 1.5f);
        event.addSource(this);
        synchronized (victims) {
            victims.add(victim.getName());
        }
        event.setModified(true);
        event.setDamage(1);
        TimeHandler.delayTime(3 * 20L, () -> victims.remove(victim.getName()));
        //shooter.sendMessage("You marked " + victim.getName() + " for " + duration + " seconds.");
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void damage(DamageApplyEvent event) {
        if(event.isCancelled() || event.getCause() != Cause.MELEE) return;
        LivingEntity entity = event.getVictim();
        synchronized (victims) {
            if (victims.contains(entity.getName()) && StatusApplier.getOrNew(entity).has(Status.MARKED) && victims.contains(entity.getName())) {
                event.addSource(this);
                event.setModified(true);
                event.setDamage(event.getDamage() + bonus);
                victims.remove(entity.getName());

                event.setVelocityModifierX(event.getVelocityModifierX() * (10D/17D));
                event.setVelocityModifierY(event.getVelocityModifierY() * (10D/17D));
                event.setVelocityModifierZ(event.getVelocityModifierZ() * (10D/17D));
            }
        }
    }
}
