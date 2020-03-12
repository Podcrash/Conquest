package me.raindance.champions.kits.skills.thief;


import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.plugin.Pluginizer;
import me.raindance.champions.Main;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Bukkit;
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
            double totalDamage = e.getDamage() - 3;
            if(totalDamage <= 0) {
                e.setDamage(0);
                e.setCancelled(true);
            }
            e.setDamage(totalDamage);
            Pluginizer.getLogger().info(getPlayer().getName() + " lightweight cancelled " + e.getDamage());
            Pluginizer.getLogger().info(e.isCancelled() + "");
        }
    }

    @Override
    public void afterConstruction() {
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1);
        boolean hasSpeed = StatusApplier.getOrNew(getPlayer()).has(Status.SPEED);

        Pluginizer.getLogger().info("[Lightweight] Attempted to apply the speed a total of " + hasSpeed + " times!");
    }

    @Override
    public void afterRespawn() {
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1);
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
