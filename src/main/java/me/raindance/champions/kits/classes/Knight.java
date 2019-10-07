package me.raindance.champions.kits.classes;

import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Knight extends ChampionsPlayer {
    public Knight(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("mob.blaze.hit", 0.95F, 57));
        this.armor = new Material[]{Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.IRON_HELMET};
    }

    public String getName() {
        return "Knight";
    }
    public SkillType getType() {
        return SkillType.Knight;
    }


}
