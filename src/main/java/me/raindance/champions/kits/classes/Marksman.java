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

public class Marksman extends ChampionsPlayer {

    public Marksman(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("random.break", 0.95F, 115));
        this.armor = new Material[]{Material.LEATHER_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.LEATHER_HELMET};
    }

    public String getName() {
        return "Marksman";
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    public SkillType getType() {
        return SkillType.Marksman;
    }


    @Override
    public int getHP() {
        return 45;
    }

    @Override
    public boolean equip() {
        if(!super.equip()) return false;
        EntityEquipment equipment = getPlayer().getEquipment();
        colorRed(equipment.getBoots());
        colorRed(equipment.getHelmet());
        return true;
    }

    private void colorRed(ItemStack leatherArmor) {
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherArmor.getItemMeta();
        meta.setColor(Color.RED);

        leatherArmor.setItemMeta(meta);
    }
}
