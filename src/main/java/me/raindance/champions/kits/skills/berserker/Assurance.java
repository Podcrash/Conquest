package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 101, skillType = SkillType.Berserker, invType = InvType.SECONDARY_PASSIVE)
public class Assurance extends Passive {
    @Override
    public String getName() {
        return "Assurance";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(getPlayer() != e.getAttacker()) return;
        LivingEntity victim = e.getVictim();
        if(victim.getMaxHealth() * 0.5D < victim.getHealth()) return;

        e.setDamage(e.getDamage() + 1);
        e.addSource(this);
        e.setModified(true);
    }
}
