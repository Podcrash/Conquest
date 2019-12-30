package me.raindance.champions.kits.skills.marksman;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.time.resources.TimeResource;
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
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SkillMetadata(skillType = SkillType.Marksman, invType = InvType.PASSIVEB)
public class ToxicTips extends Passive {
    private final int duration = 4;

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
