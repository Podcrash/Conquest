package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 106, skillType = SkillType.Berserker, invType = InvType.PASSIVEB)
public class Perserverance extends Passive {
    @Override
    public String getName() {
        return "Perserverance";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(getPlayer() != e.getVictim()) return;
        if(getPlayer().getMaxHealth() * 0.5D > getPlayer().getHealth()) return;

        e.setVelocityModifierX(e.getVelocityModifierX() * 0.9);
        e.setVelocityModifierY(e.getVelocityModifierY() * 0.9);
        e.setVelocityModifierZ(e.getVelocityModifierZ() * 0.9);
        e.setModified(true);
    }
}
