package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.hologram.Hologram;
import com.podcrash.api.mc.hologram.HologramMaker;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.PASSIVEA)
public class Conditioning extends Passive {
    private Hologram hologram;

    @Override
    public String getName() {
        return "Conditioning";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void kill(DamageApplyEvent event) {
        if(event.getAttacker() != getPlayer()) return;
        if(event.getCause() != Cause.MELEE && event.getCause() != Cause.MELEESKILL) return;
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.ABSORPTION, 40, 1, false);

        hologram = HologramMaker.createHologram(getPlayer().getEyeLocation().add(new Vector(0,0.75D, 0)), "♡");
        TimeHandler.repeatedTimeAsync(1, 0, showHearts());
    }

    private TimeResource showHearts() {
        StatusApplier applier = StatusApplier.getOrNew(getPlayer());
        return new TimeResource() {
            @Override
            public void task() {
                hologram.setLocation(getPlayer().getLocation().add(new Vector(0,0.75D, 0)));
                hologram.update();
            }

            @Override
            public boolean cancel() {
                return !applier.has(Status.ABSORPTION);
            }

            @Override
            public void cleanup() {
                hologram = null;
            }
        };
    }
}
