package me.raindance.champions.kits.classes;

import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Mage extends ChampionsPlayer {
    public Mage(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setSound(new SoundWrapper("random.break", 0.95F, 126));
        this.armor = new Material[]{Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.GOLD_CHESTPLATE, Material.GOLD_HELMET};
    }

    @Override
    public void respawn() {
        super.respawn();
        getEnergyBar().setEnergy(getEnergyBar().getMaxEnergy() * 0.75D);
    }

    public String getName() {
        return "Mage";
    }
    public SkillType getType() {
        return SkillType.Mage;
    }


    @Override
    public int getHP() {
        return 0;
    }
}
