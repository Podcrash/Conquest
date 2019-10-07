package me.raindance.champions.kits.skills.GlobalSkills;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.events.StatusApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Resistance extends Passive {
    private final float reduction;
    private final int MAX_LEVEL = 3;

    public Resistance(Player player, int level) {
        super(player, "Resistance", level, SkillType.Global, InvType.PASSIVEC);
        reduction = 0.2f + 0.15f * level;

        setDesc("Durations on you are %%reduction%% shorter for",
                "negative effects: Slowness, Screen-Shake, Nausea, Poison",
                "Blindness, Fire");

        addDescArg("reduction", () -> reduction);
        addDescArg("reduction", () -> (double)((int)(reduction * 100.0))/100.0);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler
    public void fall(StatusApplyEvent event) {
        if (event.getEntity() == getPlayer()) {
            Status status = event.getStatus();
            if (status.isNegative()) {
                event.setDuration(event.getDuration() * (1 - reduction));
                event.setModified(true);
            }
        }
    }
}
