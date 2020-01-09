package me.raindance.champions.kits.skills.duelist;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.resources.EntityParticleResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

@SkillMetadata(id = 301, skillType = SkillType.Duelist, invType = InvType.DROP)
public class ChargeForward extends Drop implements ICooldown {
    private long selfTime;
    private boolean use;

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public String getName() {
        return "Charge Forward";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    private class BullsChargeParticle extends EntityParticleResource {
        public BullsChargeParticle() {
            super(getPlayer(), ParticleGenerator.createParticle(null, EnumWrappers.Particle.CRIT, 2,0.2F,1F,0.2F), null);
        }

        @Override
        public boolean cancel() {
            return !use || System.currentTimeMillis() - getLastUsed() >= 3 * 1000L || getPlayer().isDead();
        }
    }

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if (onCooldown()) return false;
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, 3, 1);
        selfTime = System.currentTimeMillis();
        this.setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.endermen.scream", 0.75F, 10);
        use = true;
        new BullsChargeParticle().run(1, 1);
        return true;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent e) {
        if (e.isCancelled()) return;
        if (use && e.getAttacker() == getPlayer()) {
            if(e.getVictim() instanceof  Player) {
                if (1000 * 3 >= (System.currentTimeMillis() - selfTime)) {
                    Player victim = (Player) e.getVictim();
                    StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 1, 1);
                    StatusApplier.getOrNew(getPlayer()).removeVanilla(Status.SPEED);
                    SoundPlayer.sendSound(getPlayer().getLocation(), "random.break", 0.75F, 250);
                    SoundPlayer.sendSound(getPlayer().getLocation(), "mob.endermen.scream", .75F, 20);

                    getPlayer().sendMessage(getUsedMessage(e.getVictim()));
                    e.setDoKnockback(false);
                    use = false;
                }
            }
        }
    }
}
