package me.raindance.champions.kits.skills.marksman;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.*;

@SkillMetadata(id = 504, skillType = SkillType.Marksman, invType = InvType.PASSIVEB)
public class FrostArrows extends Passive implements TimeResource, IPassiveTimer {
    private Map<String, Long> affected;
    private int duration;

    public FrostArrows() {
        duration = 2;
        this.affected = new HashMap<>();
    }

    @Override
    public void start() {
        run(5, 0);
    }

    @Override
    public String getName() {
        return "Frost Arrows";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.BOW;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void shoot(DamageApplyEvent e) {
        if (!isAlly(e.getVictim()) && e.getAttacker() == getPlayer() && e.getArrow() != null && e.getCause() == Cause.PROJECTILE) {
            if(!(e.getVictim() instanceof Player)) return;
            Player player = (Player) e.getVictim();
            e.addSource(this);
            StatusApplier.getOrNew(player).applyStatus(Status.SLOW, duration, 0);
            player.setSprinting(false);
            affected.put(player.getName(), System.currentTimeMillis() + 2000L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void sprint(PlayerToggleSprintEvent event) {
        if(affected.containsKey(event.getPlayer().getName()) && event.isSprinting())
            event.setCancelled(true);
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
