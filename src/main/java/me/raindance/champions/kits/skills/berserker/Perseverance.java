package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.util.EntityUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
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
        return 9;
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
