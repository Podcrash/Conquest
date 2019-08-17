package me.raindance.champions.kits.skills.AssassinSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ShockingStrikes extends Passive {
    private final int MAX_LEVEL = 3;
    private final int duration;

    public ShockingStrikes(Player player, int level) {
        super(player, "Shocking strikes", level, SkillType.Assassin, InvType.PASSIVEB, 6 - level);
        float help = 6 - level;
        this.duration = level;
        setDesc("Your melee hits apply debuffs for %%duration%% seconds.",
                "Every hit deals Screen-Shake and Slow I.",
                "Every %%help%% seconds your next hit deals Blindness.");
        addDescArg("help", () -> help);
        addDescArg("duration", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onHit(DamageApplyEvent event) {
        if (event.isCancelled()) {
            return;
        }
        //cba with non players
        if (event.getAttacker() == getPlayer() && event.getCause() == Cause.MELEE && event.getVictim() instanceof Player) {
            Player damager = (Player) event.getAttacker();
            Player victim = (Player) event.getVictim();
            StatusApplier.getOrNew(victim).applyStatus(Status.SHOCK, duration, 1);
            StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, duration, 0);
            event.addSkillCause(this);
            if (!onCooldown()) {
                StatusApplier.getOrNew(victim).applyStatus(Status.BLIND, duration, 1);
                this.setLastUsed(System.currentTimeMillis());
            }
        }
    }
}
