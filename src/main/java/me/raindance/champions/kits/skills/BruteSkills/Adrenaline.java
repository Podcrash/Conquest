package me.raindance.champions.kits.skills.BruteSkills;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Adrenaline extends Passive {
    public Adrenaline(Player player, int level) {
        super(player, "Adrenaline", 1, SkillType.Brute, InvType.PASSIVEA);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(e.getAttacker() != getPlayer() && !(e.getVictim() instanceof Player)) return;
        if(getPlayer().getHealth()/getPlayer().getMaxHealth() >= 0.4D) return;
        e.setVelocityModifierX(e.getVelocityModifierX() * 1.05);
        e.setVelocityModifierZ(e.getVelocityModifierZ() * 1.05);
        e.setModified(true);
    }
}
