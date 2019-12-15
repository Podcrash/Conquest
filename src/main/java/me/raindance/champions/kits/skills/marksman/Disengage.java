package me.raindance.champions.kits.skills.marksman;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

@SkillMetadata(skillType = SkillType.Marksman, invType = InvType.SWORD)
public class Disengage extends Instant implements TimeResource, ICooldown {
    private final int MAX_LEVEL = 4;
    private boolean isDisengaging;
    private boolean tempFallCancel;
    private long time;
    private float effectTime;

    public Disengage() {
        effectTime = 2.5f;
    }

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public String getName() {
        return "Disengage";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (!rightClickCheck(action) || onCooldown()) return;
        isDisengaging = true;
        time = System.currentTimeMillis();
        TimeHandler.repeatedTimeAsync(1, 0, this);
        getPlayer().sendMessage(String.format("Skill> You are trying to %s", getName()));
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(DamageApplyEvent event) {
        if (isDisengaging && event.getVictim() == getPlayer() && event.getCause() == Cause.MELEE) {
            if (!(event.getAttacker() instanceof Player)) return;
            Player victim = (Player) event.getAttacker();
            event.setCancelled(true);
            isDisengaging = false;
            tempFallCancel = true;
            StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, effectTime, 3);
            Vector vector = victim.getLocation().getDirection().normalize();
            vector.multiply(0.5d + 1.35d * 0.95).setY(0.9);
            getPlayer().setVelocity(vector);
            getPlayer().sendMessage(getUsedMessage());
            setLastUsed(System.currentTimeMillis());


        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void fall(EntityDamageEvent event) {
        if (tempFallCancel && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() == getPlayer()) {
                event.setCancelled(true);
                tempFallCancel = false;
            }
        }
    }

    @Override
    public void task() {
    }

    @Override
    public boolean cancel() {
        return (System.currentTimeMillis() - time >= 999L || !getPlayer().isBlocking());
    }

    @Override
    public void cleanup() {
        if (isDisengaging) {
            getPlayer().sendMessage("Skill> You failed Disengage");
            setLastUsed(System.currentTimeMillis());
        }
        isDisengaging = false;
    }
}
