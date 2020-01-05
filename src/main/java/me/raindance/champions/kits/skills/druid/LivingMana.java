package me.raindance.champions.kits.skills.druid;

import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;

/**
 * This is a placeholder, this doesn't actually do anything
 */
@SkillMetadata(id = 202, skillType = SkillType.Druid, invType = InvType.INNATE)
public class LivingMana extends Passive {
    @Override
    public String getName() {
        return "Living Mana";
    }


}
