package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.util.EntityUtil;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 106, skillType = SkillType.Berserker, invType = InvType.SECONDARY_PASSIVE)
public class Perseverance extends Passive implements ICooldown {
    @Override
    public String getName() {
        return "Perseverance";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public float getCooldown() {
        return 6;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(getPlayer() != e.getVictim()) return;
        if(!EntityUtil.isBelow(getPlayer(), 0.4)) return;

        if(onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.REGENERATION, 3, 2, true, true);
    }
}
