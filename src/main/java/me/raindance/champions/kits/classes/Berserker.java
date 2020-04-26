package me.raindance.champions.kits.classes;

import com.podcrash.api.sound.SoundWrapper;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.Skill;
import me.raindance.champions.kits.SkillType;
import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class Berserker extends ChampionsPlayer {
    public Berserker(Player player, List<Skill> skills) {
        super(player, 40);
        this.skills = new HashSet<>(skills);
        setSound(new SoundWrapper("mob.blaze.hit", 0.95F, 66));
        this.armor = new Material[]{Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS};
    }

    @Override
    public void effects() {
        this.setUsesEnergy(true, 4);
        getEnergyBar().toggleRegen(false); //no natural gain of energy
        getEnergyBar().setEnergy(0);
    }

    @Override
    public SkillType getType() {
        return SkillType.Berserker;
    }
}
