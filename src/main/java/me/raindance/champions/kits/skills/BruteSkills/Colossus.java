package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class Colossus extends Passive {
    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getSkillTokenWeight() {
        return 2;
    }

    public Colossus(Player player, int level) {
        super(player, "Colossus", level, SkillType.Brute, InvType.PASSIVEB);
        setDesc(Arrays.asList(
                "You are so huge that you take ",
                "33% less knockback from attacks ",
                "and while sneaking you take no ",
                "knockback."
        ));
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void hit(DamageApplyEvent e) {
        if (e.isCancelled()) return;
        if (e.getVictim() == getPlayer()) {
            e.setModified(true);
            e.setVelocityModifierX(0.65D);
            e.setVelocityModifierZ(0.65D);
        } else if (e.getAttacker() == getPlayer()) {
            e.setModified(true);
            e.setDamage(e.getDamage() - 0.5D);
        }
    }
}
