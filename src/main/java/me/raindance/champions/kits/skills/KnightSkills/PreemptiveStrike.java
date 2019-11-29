package me.raindance.champions.kits.skills.KnightSkills;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PreemptiveStrike extends Passive {
    public PreemptiveStrike(Player player, int level) {
        super(player, "Preemptive Strike", 1, SkillType.Knight, InvType.PASSIVEB, 10);
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(onCooldown() || e.getAttacker() != getPlayer()) return;
        if(!(e.getVictim() instanceof Player)) return;
        setLastUsed(System.currentTimeMillis());
        StatusApplier.getOrNew((Player) e.getVictim()).applyStatus(Status.WEAKNESS, 4, 1);
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.guardian.curse", 0.9F, 90);

    }
    @Override
    public int getMaxLevel() {
        return 1;
    }
}
