package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.util.EntityUtil;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;
import me.raindance.champions.annotation.kits.SkillMetadata;

@SkillMetadata(id = 901, skillType = SkillType.Warden, invType = InvType.PRIMARY_PASSIVE)
public class Adrenaline extends Passive {
    @Override
    public String getName() {
        return "Adrenaline";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getAttacker() != getPlayer()) return;
        if(EntityUtil.isBelow(getPlayer(), 0.4) && e.getCause().equals(Cause.MELEE)) {
            e.setVelocityModifierX(e.getVelocityModifierX() * 1.33);
            e.setVelocityModifierZ(e.getVelocityModifierZ() * 1.33);
            e.addSource(this);
            e.setModified(true);
        }
    }
}
