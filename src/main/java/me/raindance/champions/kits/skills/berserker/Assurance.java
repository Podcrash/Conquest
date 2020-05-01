package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.kits.KitPlayerManager;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.Bukkit;
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

        // Find the current percentage of health remaining, then multiply it by our real max HP value (e.g. 40 for zerk right now)
        double trueCurrentHP = (e.getVictim().getHealth() / e.getVictim().getMaxHealth()) * getChampionsPlayer().getHP();
        double trueMissingHP = getChampionsPlayer().getHP() - trueCurrentHP;
        double bonus = trueMissingHP * 0.1;

        e.setDamage(e.getDamage() + bonus);
        e.setModified(true);
    }
}
