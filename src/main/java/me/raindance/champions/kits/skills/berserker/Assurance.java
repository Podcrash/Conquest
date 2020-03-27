package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 101, skillType = SkillType.Berserker, invType = InvType.PASSIVEB)
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
