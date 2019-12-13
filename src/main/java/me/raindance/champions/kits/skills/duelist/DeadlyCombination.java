package me.raindance.champions.kits.skills.duelist;

import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.entity.Entity;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.SWORD)
public class DeadlyCombination extends Interaction implements ICooldown {
    @Override
    public void doSkill(Entity clickedEntity) {

    }

    @Override
    public float getCooldown() {
        return 14;
    }

    @Override
    public String getName() {
        return "Deadly Combination";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
