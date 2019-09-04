package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.effect.status.StatusWrapper;
import me.raindance.champions.effect.status.ThrowableStatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import me.raindance.champions.listeners.GameDamagerConverterListener;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PinDown extends Instant {
    private Arrow arrow;
    private Set<String> affected = new HashSet<>();
    private int duration;

    public PinDown(Player player, int level) {
        super(player, "Pin Down", level, SkillType.Ranger, ItemType.BOW, InvType.BOW, 13 - level);
        this.duration = 2 + level;
        setDesc(Arrays.asList(
                "Instantly fire an arrow, giving ",
                "target Slow 4 for %%duration%% seconds. "
        ));
        addDescArg("duration", () ->  duration);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (!onCooldown()){
                arrow = getPlayer().launchProjectile(Arrow.class);
                arrow.setVelocity(arrow.getVelocity());
                GameDamagerConverterListener.forceAddArrow(arrow, 0.375f);
                arrow.setShooter(getPlayer());
                ThrowableStatusApplier.applyProj(new StatusWrapper(Status.ROOTED, this.duration, 1, false), arrow);
                ThrowableStatusApplier.applyProj(new StatusWrapper(Status.SILENCE, this.duration, 1, false), arrow);
                ThrowableStatusApplier.applyProj(new StatusWrapper(Status.SLOW, this.duration, 2, false), arrow);
                getPlayer().sendMessage(getUsedMessage());
                this.setLastUsed(System.currentTimeMillis());
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void arrowHit(DamageApplyEvent e) {
        if(isAlly(e.getVictim())) return;
        if (e.getArrow() == arrow) {
            e.setDoKnockback(false);
            affected.add(e.getVictim().getName());
        } else if (affected.contains(e.getVictim().getName())) {
            affected.remove(e.getVictim().getName());
            if(e.getVictim() instanceof Player)
                StatusApplier.getOrNew((Player) e.getVictim()).removeStatus(Status.SLOW, Status.SILENCE, Status.ROOTED);
        }

    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
