package me.raindance.champions.kits.skills.BruteSkills;

import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Condemn extends Passive {
    public Condemn(Player player, int level) {
        super(player, "Condemn", 1, SkillType.Brute, InvType.PASSIVEA, 18);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @EventHandler
    public void damage(DamageApplyEvent e) {
        if(onCooldown() || e.getAttacker() != getPlayer()) return;
        if(!(e.getVictim() instanceof Player)) return;
        setLastUsed(System.currentTimeMillis());
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.GROUND, 1.5F, 1);
        e.setDamage(e.getDamage() - 2);
        e.setModified(true);
        ParticleGenerator.createBlockEffect(getPlayer().getLocation(), Material.WOODEN_DOOR.getId());
    }
}
