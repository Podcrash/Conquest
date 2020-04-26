package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.events.StatusApplyEvent;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

//@SkillMetadata(id = 310, skillType = SkillType.Duelist, invType = InvType.PRIMARY_PASSIVE)
public class Tenacious extends Passive {
    @Override
    public String getName() {
        return "Tenacious";
    }

    @EventHandler
    public void onStatusApply(StatusApplyEvent event) {
        if (event.getEntity() == getPlayer()) {
            Status status = event.getStatus();
            if (status.isNegative()) {
                event.setDuration(event.getDuration() / 2);
                event.setModified(true);
            }
        }
    }
}
