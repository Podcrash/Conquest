package me.raindance.champions.kits.classes;

import com.podcrash.api.mc.sound.SoundWrapper;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Vanguard extends ChampionsPlayer {

    public Vanguard(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("mob.blaze.hit", 0.95F, 57));
        this.armor = new Material[]{Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_HELMET};
    }

    public SkillType getType() {
        return SkillType.Vanguard;
    }

    @Override
    public int getHP() {
        return 55;
    }
}