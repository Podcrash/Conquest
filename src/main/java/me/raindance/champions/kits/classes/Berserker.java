package me.raindance.champions.kits.classes;

import com.podcrash.api.mc.sound.SoundWrapper;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Berserker extends ChampionsPlayer {
    public Berserker(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("mob.blaze.hit", 0.95F, 57));
        this.armor = new Material[]{Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS};
    }

    @Override
    public void effects() {
        this.setUsesEnergy(true, 4);
        getEnergyBar().stop(); //no natural gain of energy
    }

    @Override
    public SkillType getType() {
        return SkillType.Berserker;
    }

    @Override
    public int getHP() {
        return 35;
    }
}
