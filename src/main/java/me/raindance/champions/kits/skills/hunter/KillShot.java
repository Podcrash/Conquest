package me.raindance.champions.kits.skills.hunter;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.util.EntityUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Hunter, invType = InvType.PASSIVEA)
public class KillShot extends Passive {
    @Override
    public String getName() {
        return "KillShot";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void bowHit(DamageApplyEvent event) {
        if(event.getAttacker() != getPlayer() || event.getCause() != Cause.PROJECTILE || isAlly(event.getVictim())) return;
        LivingEntity entity = event.getVictim();

        if(!EntityUtil.isBelow(entity, 0.4)) return;

        event.setModified(true);
        event.setDamage(event.getDamage() + 4);
    }
}
