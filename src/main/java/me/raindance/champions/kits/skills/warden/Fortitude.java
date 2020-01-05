package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillInit;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

@SkillMetadata(id = 906, skillType = SkillType.Warden, invType = InvType.PASSIVEB)
public class Fortitude extends Passive implements ICooldown, IConstruct {
    private TimeResource resource;

    @Override
    public void afterConstruction() {
        resource = new TimeResource() {
            @Override
            public void task() {
                StatusApplier.getOrNew(getPlayer()).applyStatus(Status.REGENERATION, 3, 0);
            }

            @Override
            public boolean cancel() {
                return false;
            }

            @Override
            public void cleanup() {

            }
        };
    }

    @Override
    public String getName() {
        return "Fortitude";
    }

    @Override
    public float getCooldown() {
        return 4.0F;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if (event.getVictim() == getPlayer() && event.getCause() == Cause.MELEE) {
            hit();
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void hit(EntityDamageEvent event) {
        if(event.isCancelled()) return;
        if (event.getEntity() == getPlayer() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            hit();
        }
    }

    public void hit() {
        if(onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        TimeHandler.unregister(resource);
        TimeHandler.delayTime(5, resource);
    }
}
