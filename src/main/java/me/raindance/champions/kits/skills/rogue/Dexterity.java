package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
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
        if(e.getAttacker() == getPlayer() && !isAlly(e.getVictim())) {
            if(e.getCause() != Cause.MELEE) return;
            e.setDoKnockback(false);
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, 3, 0, true, true);
        }
    }
}
