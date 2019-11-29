package me.raindance.champions.kits.skills.BruteSkills;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Titan extends Passive {
    public Titan(Player player, int level) {
        super(player, "Titan", 1, SkillType.Brute, InvType.PASSIVEA);
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getVictim() != getPlayer() && e.getCause() != Cause.PROJECTILE) return;
        e.setDamage(e.getDamage() * 0.75D);
        e.setModified(true);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
