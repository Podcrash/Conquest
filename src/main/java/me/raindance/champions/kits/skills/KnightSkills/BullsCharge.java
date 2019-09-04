package me.raindance.champions.kits.skills.KnightSkills;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.time.resources.EntityParticleResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class BullsCharge extends Instant {
    private final int MAX_LEVEL = 5;
    private final int selfduration;
    private final float duration;
    private long selfTime;
    private boolean use;

    public BullsCharge(Player player, int level) {
        super(player, "Bulls Charge", level, SkillType.Knight, ItemType.AXE, InvType.AXE, 10 + level);

        selfduration = 3 + level;
        duration = 2.5f + 0.5f * level;

        setDesc(Arrays.asList(
                "Charge forwards with Speed 2 for ",
                "%%time%% seconds. If you attack during this ",
                "time, your target receives Slow 2 ",
                "for %%time2%% seconds, as well as no knockback. "
        ));
        addDescArg("time", () ->  selfduration);
        addDescArg("time2", () -> duration);
    }

    private class BullsChargeParticle extends EntityParticleResource {
        public BullsChargeParticle() {
            super(getPlayer(), ParticleGenerator.createParticle(null, EnumWrappers.Particle.CRIT, 2,0.2F,1F,0.2F), null);
        }

        @Override
        public boolean cancel() {
            return !use || System.currentTimeMillis() - getLastUsed() >= selfduration * 1000L || getPlayer().isDead();
        }
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!onCooldown()) {
                getPlayer().sendMessage(getUsedMessage());
                StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, selfduration, 1);
                selfTime = System.currentTimeMillis();
                this.setLastUsed(System.currentTimeMillis());
                SoundPlayer.sendSound(getPlayer().getLocation(), "mob.endermen.scream", 0.75F, 10);
                use = true;
                new BullsChargeParticle().run(1, 1);
            }
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent e) {
        if (e.isCancelled()) return;
        if (use && e.getAttacker() == getPlayer()) {
            if(e.getVictim() instanceof  Player) {
                if (1000 * selfduration >= (System.currentTimeMillis() - selfTime)) {
                    Player victim = (Player) e.getVictim();
                    StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, duration, 1);
                    StatusApplier.getOrNew(getPlayer()).removeVanilla(Status.SPEED);
                    SoundPlayer.sendSound(getPlayer().getLocation(), "random.break", 0.75F, 250);
                    SoundPlayer.sendSound(getPlayer().getLocation(), "mob.endermen.scream", .75F, 20);

                    e.setDoKnockback(false);
                    use = false;
                }
            }
        }
    }
}
