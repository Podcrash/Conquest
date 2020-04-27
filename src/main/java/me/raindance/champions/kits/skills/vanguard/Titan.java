package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;


@SkillMetadata(id = 808, skillType = SkillType.Vanguard, invType = InvType.PRIMARY_PASSIVE)
public class Titan extends Passive {
    @Override
    public String getName() {
        return "Titan";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getVictim() != getPlayer() || e.getCause() != Cause.PROJECTILE) return;
        e.setDamage(e.getDamage() * 0.75D);
        e.setModified(true);
    }
}
