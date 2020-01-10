package me.raindance.champions.kits.classes;

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

public class Sorcerer extends ChampionsPlayer {
    public Sorcerer(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        this.armor = new Material[]{Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET};
    }

    @Override
    public void effects() {
        super.effects();
        this.setUsesEnergy(true, 200);
        getEnergyBar().toggleRegen(true);
        getEnergyBar().setNaturalRegenRate(2);
    }

    @Override
    public SkillType getType() {
        return SkillType.Sorcerer;
    }

    @Override
    public int getHP() {
        return 35;
    }
}
