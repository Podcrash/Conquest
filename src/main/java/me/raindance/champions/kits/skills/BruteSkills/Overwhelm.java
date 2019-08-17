package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class Overwhelm extends Passive {
    private final int SKILL_TOKEN_WEIGHT = 1;
    private final int MAX_LEVEL = 3;
    private float maxBonus = 0;

    public Overwhelm(Player player, int level) {
        super(player, "Overwhelm", level, SkillType.Brute, InvType.PASSIVEB);
        maxBonus = 0.5f + 0.5f * level;

        setDesc(Arrays.asList(
                "For every one health you have more ",
                "than your target, you deal 0.25 ",
                "bonus damage.",
                "",
                "Maximum of %%damage%% bonus damage."
        ));
        addDescArg("damage", () ->  maxBonus);
    }

    @Override
    public int getSkillTokenWeight() {
        return SKILL_TOKEN_WEIGHT;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    public void hit(DamageApplyEvent e) {
        if (e.getAttacker() == getPlayer()) {
            Player damager = (Player) e.getAttacker();
            LivingEntity victim = e.getVictim();
            double diff = (damager.getHealth() - victim.getHealth());
            double bonus = diff * 0.25d;
            if (bonus <= 0) {
                return;
            } else if (bonus > maxBonus) {
                bonus = maxBonus;
            }
            e.setModified(true);
            e.addSkillCause(this);
            e.setDamage(e.getDamage() + bonus);
        }
    }
}