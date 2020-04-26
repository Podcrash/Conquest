package me.raindance.champions.kits.classes;

import com.podcrash.api.sound.SoundWrapper;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Warden extends ChampionsPlayer {

    public Warden(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("mob.blaze.hit", 0.95F, 57));
        this.armor = new Material[]{Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.IRON_HELMET};
    }

    public SkillType getType() {
        return SkillType.Warden;
    }

    @Override
    public int getHP() {
        return 60;
    }
}