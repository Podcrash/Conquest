package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

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

    @EventHandler
    public void skill(SkillUseEvent e) {
        if(e.isCancelled()) return;
        if(e.getPlayer() == getPlayer()) {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.WEAKNESS, 3, 1);
        }
    }
}
