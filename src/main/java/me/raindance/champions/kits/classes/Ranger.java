package me.raindance.champions.kits.classes;

import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.sound.SoundWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Ranger extends ChampionsPlayer {

    public Ranger(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("random.break", 0.65F, 115));
        this.armor = new Material[]{Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET};
    }

    public String getName() {
        return "Ranger";
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    public SkillType getType() {
        return SkillType.Ranger;
    }
}
