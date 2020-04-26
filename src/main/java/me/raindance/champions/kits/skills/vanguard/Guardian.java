package me.raindance.champions.kits.skills.vanguard;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Random;

@SkillMetadata(id = 804, skillType = SkillType.Vanguard, invType = InvType.DROP)
public class Guardian extends Drop implements ICooldown {
    private final double radiusSquared = 5 * 5;
    private final float duration = 5;
    private boolean active;
    @Override
    public String getName() {
        return "Guardian";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public float getCooldown() {
        return 17;
    }

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        active = true;
        StatusApplier.getOrNew(e.getPlayer()).applyStatus(Status.RESISTANCE, duration, 0);
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.irongolem.death", 0.85F, 92);
        new GuardianProtect().run(2, 0);
        return true;
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(!active) return;
        if(e.getVictim() == getPlayer()) {
            e.setModified(true);
            return;
        }
        if(!isAlly(e.getVictim()) || e.getCause() != Cause.MELEE) return;
        if(e.getVictim().getLocation().distanceSquared(getPlayer().getLocation()) >= radiusSquared) return;
        e.setDamage(.8D * e.getDamage());
        e.setModified(true);

        DamageApplier.damage(getPlayer(), e.getAttacker(), .2D * e.getDamage(), this, true);

    }

    private final class GuardianProtect implements TimeResource {
        private Random rand;
        private GuardianProtect() {
            rand = new Random();
        }
        @Override
        public void task() {
            WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(),
                    EnumWrappers.Particle.SPELL_MOB, new int[]{211, 211, 211}, 5,
                    rand.nextFloat() / 2f, 0.25f + (rand.nextFloat() - 0.15f), rand.nextFloat() / 2f);
            getPlayer().getWorld().getPlayers().forEach(player -> ParticleGenerator.generate(player, particle));
        }

        @Override
        public boolean cancel() {
            return !active || System.currentTimeMillis() - getLastUsed() >= (long) duration * 1000L;
        }

        @Override
        public void cleanup() {
            active = false;
        }
    }

}
