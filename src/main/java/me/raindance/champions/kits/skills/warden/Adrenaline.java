package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import me.raindance.champions.kits.annotation.SkillMetadata;

@SkillMetadata(skillType = SkillType.Warden, invType = InvType.PASSIVEA)
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
        if(e.getAttacker() != getPlayer() && !(e.getVictim() instanceof Player)) return;
        if(getPlayer().getHealth()/getPlayer().getMaxHealth() >= 0.4D) return;
        e.setVelocityModifierX(e.getVelocityModifierX() * 1.05);
        e.setVelocityModifierZ(e.getVelocityModifierZ() * 1.05);
        e.setModified(true);
    }
}
