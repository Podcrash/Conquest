package me.raindance.champions.kits.classes;

import com.podcrash.api.sound.SoundWrapper;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.Skill;
import me.raindance.champions.kits.SkillType;
import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Sorcerer extends ChampionsPlayer {
    public Sorcerer(Player player, List<Skill> skills) {
        super(player, 45);
        this.skills = new HashSet<>(skills);
        setSound(new SoundWrapper("random.break", 0.95F, 90));
        this.armor = new Material[]{Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET};
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
        return SkillType.Sorcerer;
    }
}
