package me.raindance.champions.kits.classes;

import com.podcrash.api.mc.sound.SoundWrapper;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class Rogue extends ChampionsPlayer {
    public Rogue(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("random.bow", 0.95F, 126));
        this.armor = new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};
    }

    @Override
    public SkillType getType() {
        return SkillType.Rogue;
    }

    @Override
    public int getHP() {
        return 35;
    }

    @Override
    public boolean equip() {
        if(!super.equip()) return false;
        EntityEquipment equipment = getPlayer().getEquipment();
        for(ItemStack armor : equipment.getArmorContents()) {
            if(armor.getType() == Material.LEATHER_BOOTS ||
                    armor.getType() == Material.LEATHER_LEGGINGS) continue;
            colorWhite(armor);
        }
        return true;
    }

    //TODO: itemstackutil methods for this
    private void colorWhite(ItemStack leatherArmor) {
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherArmor.getItemMeta();
        meta.setColor(Color.WHITE);

        leatherArmor.setItemMeta(meta);
    }
}
