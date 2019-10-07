package me.raindance.champions.kits.classes;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.sound.SoundWrapper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class Assassin extends ChampionsPlayer {
    public Assassin(Player player, List<Skill> skills) {
        super(player);
        this.skills = skills;
        setFallDamage(1.5d);
        setSound(new SoundWrapper("random.bow", 0.95F, 126));
        this.armor = new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String getName() {
        return "Assassin";
    }
    public SkillType getType() {
        return SkillType.Assassin;
    }

    @Override
    public void respawn() {
        super.respawn();
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SPEED, Integer.MAX_VALUE, 1, true, true);
    }
}
