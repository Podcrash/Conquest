package me.raindance.champions.kits.skills.marksman;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SkillMetadata(id = 507, skillType = SkillType.Marksman, invType = InvType.BOW)
public class MarkedForDeath extends BowShotSkill {
    private Set<String> victims;
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
        StatusApplier.getOrNew(victim).applyStatus(Status.MARKED, 4, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BLAZE_BREATH, 1.0f, 1.5f);
        event.addSource(this);
        victims.add(victim.getName());
        //shooter.sendMessage("You marked " + victim.getName() + " for " + duration + " seconds.");
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {

    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        LivingEntity entity = event.getVictim();
        if(victims.contains(entity.getName()) && StatusApplier.getOrNew(entity).has(Status.MARKED)) {
            event.addSource(this);
            event.setModified(true);
            event.setDamage(event.getDamage() + 2);
        }
    }
}
