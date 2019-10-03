package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.*;

public class BarbedArrows extends Passive implements TimeResource {
    private Map<String, Long> affected;
    private int duration;

    public BarbedArrows(Player player, int level) {
        super(player, "Barbed Arrows", level,  SkillType.Ranger, InvType.PASSIVEB);
        duration = level;
        setDesc(Arrays.asList(
                "Your arrows are barbed, and give ",
                "opponents Slow 1 for %%duration%% seconds. ",
                "Will cancel sprint on opponents. "
        ));
        addDescArg("duration", () ->  duration);
        this.affected = new HashMap<>();

    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void shoot(DamageApplyEvent e) {
        if (!isAlly(e.getVictim()) && e.getAttacker() == getPlayer() && e.getArrow() != null && e.getCause() == Cause.PROJECTILE) {
            if(!(e.getVictim() instanceof Player)) return;
            Player player = (Player) e.getVictim();
            e.addSkillCause(this);
            StatusApplier.getOrNew(player).applyStatus(Status.SLOW, duration, 1);
            player.setSprinting(false);
            affected.put(player.getName(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void sprint(PlayerToggleSprintEvent event) {
        if(affected.containsKey(event.getPlayer().getName()) && event.isSprinting())
            event.setCancelled(true);
    }
    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void task() {
        affected.values().removeIf(value -> System.currentTimeMillis() >= value);
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {
        this.affected.clear();
    }
}
