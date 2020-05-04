package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.skill.SkillUseEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SkillMetadata(id = 803, skillType = SkillType.Vanguard, invType = InvType.INNATE)
public class Exhaustion extends Passive {
    @Override
    public String getName() {
        return "Exhaustion";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void skill(SkillUseEvent e) {
        if(e.isCancelled()) return;
        if(e.getPlayer() == getPlayer()) {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.WEAKNESS, 3, 0);
        }
    }
}
