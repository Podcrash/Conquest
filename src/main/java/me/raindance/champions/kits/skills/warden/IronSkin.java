package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SkillMetadata(id = 907, skillType = SkillType.Warden, invType = InvType.SECONDARY_PASSIVE)
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
            setLastUsed(System.currentTimeMillis());
            getPlayer().sendMessage(getUsedMessage());
            double subtract = event.getDamage() - 3;
            event.setDamage(Math.max(subtract, 0));
            event.setModified(true);
        }
    }
}
