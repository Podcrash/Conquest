package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;


@SkillMetadata(id = 808, skillType = SkillType.Vanguard, invType = InvType.PASSIVEA)
public class Titan extends Passive {
    @Override
    public String getName() {
        return "Titan";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getVictim() != getPlayer() || e.getCause() != Cause.PROJECTILE) return;
        e.setDamage(e.getDamage() * 0.75D);
        e.setModified(true);
    }
}
