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

public class Druid extends ChampionsPlayer {
    public Druid(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("random.break", 0.95F, 90));
        this.armor = new Material[]{Material.LEATHER_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.LEATHER_HELMET};
    }

    @Override
    public void effects() {
        super.effects();
        this.setUsesEnergy(true, 200);
        getEnergyBar().toggleRegen(true);
        double ratePerSecond = 10D;
        double ratePerTick = ratePerSecond / 20D;
        getEnergyBar().setNaturalRegenRate(ratePerTick);
    }

    @Override
    public SkillType getType() {
        return SkillType.Druid;
    }

    @Override
    public boolean equip() {
        if(!super.equip()) return false;
        EntityEquipment equipment = getPlayer().getEquipment();
        for(ItemStack armor : equipment.getArmorContents()) {
            if(armor.getType() == Material.GOLD_CHESTPLATE ||
                    armor.getType() == Material.GOLD_LEGGINGS) continue;
            colorGreen(armor);
        }
        return true;
    }

    @Override
    public int getHP() {
        return 30;
    }

    //TODO: itemstackutil methods for this
    private void colorGreen(ItemStack leatherArmor) {
        LeatherArmorMeta meta = (LeatherArmorMeta) leatherArmor.getItemMeta();
        meta.setColor(Color.GREEN);

        leatherArmor.setItemMeta(meta);
    }
}
