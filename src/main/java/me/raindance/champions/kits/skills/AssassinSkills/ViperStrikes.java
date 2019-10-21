package me.raindance.champions.kits.skills.AssassinSkills;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ViperStrikes extends Passive {
    private final int duration;
    private final int MAX_LEVEL = 3;

    public ViperStrikes(Player player, int level) {
        super(player, "Viper Strikes", level, SkillType.Assassin, InvType.PASSIVEB);
        duration = 2 + 2 * level;
        setDesc("Your attacks give enemies Poison I and Wither I for %%duration%% seconds.");
        addDescArg("duration", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onHit(DamageApplyEvent event) {
        if (event.isCancelled()) return;
        //cba with non players
        if (event.getAttacker() == getPlayer() && event.getCause() == Cause.MELEE && event.getVictim() instanceof Player) {
            Player victim = (Player) event.getVictim();
            StatusApplier.getOrNew(victim).applyStatus(Status.POISON, duration, 0, false, true);
            StatusApplier.getOrNew(victim).applyStatus(Status.WITHER, duration, 0, false, true);
            SoundPlayer.sendSound(victim.getLocation(), "mob.spider.say", 0.75F, 140, getPlayers());
            event.addSource(this);
        }
    }
}
