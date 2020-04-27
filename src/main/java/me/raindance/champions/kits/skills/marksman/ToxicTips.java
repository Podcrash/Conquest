package me.raindance.champions.kits.skills.marksman;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

@SkillMetadata(id = 510, skillType = SkillType.Marksman, invType = InvType.SECONDARY_PASSIVE)
public class ToxicTips extends Passive {
    private final int duration = 3;

    public ToxicTips() {
        super();
    }

    @Override
    public String getName() {
        return "Toxic Tips";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.BOW;
    }

    @EventHandler
    public void shootBow(EntityShootBowEvent e) {
        if(e.getEntity() == getPlayer()) {
            WrapperPlayServerWorldParticles packet =
                    ParticleGenerator.createParticle(e.getProjectile().getLocation().toVector(), EnumWrappers.Particle.SPELL_MOB, new int[]{0, 255,0}, 2,0,0,0);
            ParticleGenerator.generateProjectile((Projectile) e.getProjectile(), packet);
        }
    }
    @EventHandler(
            priority = EventPriority.LOW
    )
    public void shoot(DamageApplyEvent e) {
        if (!isAlly(e.getVictim()) && e.getAttacker() == getPlayer() && e.getArrow() != null && e.getCause() == Cause.PROJECTILE) {
            if(!(e.getVictim() instanceof Player)) return;
            Player player = (Player) e.getVictim();
            e.addSource(this);
            StatusApplier.getOrNew(player).applyStatus(Status.POISON, duration, 0);
        }
    }
}
