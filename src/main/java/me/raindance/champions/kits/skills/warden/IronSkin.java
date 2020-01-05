package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SkillMetadata(id = 907, skillType = SkillType.Warden, invType = InvType.PASSIVEB)
public class IronSkin extends Passive implements ICooldown {
    @Override
    public float getCooldown() {
        return 9;
    }

    @Override
    public String getName() {
        return "Iron Skin";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent event) {
        if(onCooldown()) return;
        if(event.isCancelled()) return;
        if (event.getCause() != Cause.MELEE) return;
        if (event.getVictim() == getPlayer()) {
            event.setModified(true);
            setLastUsed(System.currentTimeMillis());
            getPlayer().sendMessage(getUsedMessage());
            double subtract = event.getDamage() - 3;
            if (subtract < 0) subtract = 0;
            event.setDamage(subtract);
        }
    }
}
