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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 502, skillType = SkillType.Marksman, invType = InvType.SWORD)
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
        return 12;
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
    protected void doSkill(PlayerEvent event, Action action) {
        if (!rightClickCheck(action) || onCooldown()) return;
        isDisengaging = true;
        time = System.currentTimeMillis();
        TimeHandler.repeatedTimeAsync(1, 0, this);
        getPlayer().sendMessage(getUsedMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void hit(DamageApplyEvent event) {
        if (isDisengaging && event.getVictim() == getPlayer() && event.getCause() == Cause.MELEE) {
            if (isAlly(event.getAttacker())) return;
            LivingEntity victim = event.getAttacker();
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
            priority = EventPriority.LOW
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
            getPlayer().sendMessage(getFailedMessage());
            setLastUsed(System.currentTimeMillis());
        }
        isDisengaging = false;
    }
}
