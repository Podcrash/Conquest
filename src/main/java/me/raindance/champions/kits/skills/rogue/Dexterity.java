package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

/**
 * Dexterity
 * Class: Rogue
 * Type: Secondary Passive
 * Cooldown: N/A
 * Description: Your melee attacks grant you Speed I for 3 seconds.
 */
@SkillMetadata(id = 603, skillType = SkillType.Rogue, invType = InvType.INNATE)
public class Dexterity extends Passive {
    @Override
    public String getName() {
        return "Dexterity";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getAttacker() == getPlayer() && !isAlly(e.getVictim()))
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, 3, 0);
    }
}
