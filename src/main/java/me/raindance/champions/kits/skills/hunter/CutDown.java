package me.raindance.champions.kits.skills.hunter;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.util.EntityUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

@SkillMetadata(id = 403, skillType = SkillType.Hunter, invType = InvType.PRIMARY_PASSIVE)
public class CutDown extends Passive {
    private final int duration = 4;

    public CutDown() {
        super();
    }

    @Override
    public String getName() {
        return "Cut Down";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.BOW;
    }

    @EventHandler
    public void shootBow(EntityShootBowEvent e) {
        if(e.getEntity() == getPlayer()) {
            WrapperPlayServerWorldParticles packet =
                    ParticleGenerator.createParticle(e.getProjectile().getLocation().toVector(), EnumWrappers.Particle.SPELL_INSTANT, new int[]{0, 255,0}, 2,0,0,0);
            ParticleGenerator.generateProjectile((Projectile) e.getProjectile(), packet);
        }
    }
    @EventHandler(priority = EventPriority.LOW)
    public void shoot(DamageApplyEvent e) {
        if (!isAlly(e.getVictim()) && e.getAttacker() == getPlayer() && e.getArrow() != null && e.getCause() == Cause.PROJECTILE) {
            if(isAlly(e.getVictim())) {
                e.setCancelled(true);
                return;
            }
            if(!(e.getVictim() instanceof Player)) return;
            Player player = (Player) e.getVictim();
            if(EntityUtil.isBelow(player, 0.75)) return;
            e.setDamage(e.getDamage() + 3);
            e.addSource(this);
        }
    }
}
