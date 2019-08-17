package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class SeismicBlade extends Passive implements IEnergy {
    private final int MAX_LEVEL = 3;

    public SeismicBlade(Player player, int level) {
        super(player, "Seismic Blade", level, SkillType.Mage, InvType.PASSIVEB);
        setDesc(Arrays.asList(
                "While grounded, your attacks will ",
                "deal more knockback to any opponent. ",
                "",
                "However, you will not be able to deal ",
                "any damage if you run out of Energy."
        ));
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void hit(DamageApplyEvent devent) {
        if (devent.getAttacker() == getPlayer()) {
            if (((Entity) getPlayer()).isOnGround() && hasEnergy()) {
                devent.setModified(true);
                devent.setVelocityModifierX(1.5D);
                devent.setVelocityModifierY(1.5D);
                devent.setVelocityModifierZ(1.5D);
                useEnergy(getEnergyUsage());
                devent.addSkillCause(this);
            } else {
                devent.setDoKnockback(false);
                devent.setCancelled(true);
            }
        }
    }


    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getEnergyUsage() {
        return 65 - getLevel() * 5;
    }
}
