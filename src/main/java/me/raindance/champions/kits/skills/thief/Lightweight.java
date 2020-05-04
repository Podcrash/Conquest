package me.raindance.champions.kits.skills.thief;


import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.events.game.GameStartEvent;
import com.podcrash.api.time.TimeHandler;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.IConstruct;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

@SkillMetadata(id = 706, skillType = SkillType.Thief, invType = InvType.INNATE)
public class Lightweight extends Passive implements IConstruct {
    @EventHandler
    public void hit(DamageApplyEvent event) {
        //no knockback clause
        if(event.getAttacker() != getPlayer() || event.getCause() != Cause.MELEE) return;

        event.setDoKnockback(false);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void fall(EntityDamageEvent e) {
        if(getPlayer() == e.getEntity() && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double totalDamage = e.getDamage() - 2;
            if(totalDamage <= 0) {
                e.setDamage(0);
                e.setCancelled(true);
            }
            e.setDamage(totalDamage);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onStart(GameStartEvent e) {
        TimeHandler.delayTime(30, () -> {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1, true);
        });
    }

    @Override
    public void afterConstruction() {
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1, true);
    }

    @Override
    public void afterRespawn() {
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1, true);
    }

    @Override
    public String getName() {
        return "Lightweight";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }
}
