package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Passive extends Skill {
    public Passive(Player player, String name, int level, SkillType type, InvType invType, float cooldown) {
        super(player, name, level, type, null, invType, cooldown);
    }
    public Passive(Player player, String name, int level, SkillType type, InvType invType) {
        this(player, name, level, type, invType, -1);
    }
    /*
    Returns true if drops sword, bow, shovel, or axe
     */
    protected final boolean checkItem(ItemStack itemStack){
        for(ItemType itemtype : ItemType.values()){
            if(itemStack.getType().name().toLowerCase().contains(itemtype.getName().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    protected final ItemType getItemType(ItemStack itemStack){
        for(ItemType itemtype : ItemType.values()){
            if(itemStack.getType().name().toLowerCase().contains(itemtype.getName().toLowerCase())){
                return itemtype;
            }
        }
        return null;
    }
}
