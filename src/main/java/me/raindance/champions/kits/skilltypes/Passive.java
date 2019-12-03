package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//TODO: Make ActiveSkill (DropQ)
public abstract class Passive extends Skill {
    public Passive() {
        super();
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
