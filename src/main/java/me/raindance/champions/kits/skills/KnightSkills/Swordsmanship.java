package me.raindance.champions.kits.skills.KnightSkills;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.time.TimeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class Swordsmanship extends Passive implements ICharge, IPassiveTimer {
    private int charges = 0;
    private final int MAX_LEVEL = 3;

    public Swordsmanship(Player player, int level) {
        super(player, "Swordsmanship", level, SkillType.Knight, InvType.PASSIVEA);
        charges = level + 1;
        setDesc(Arrays.asList(
                "Prepare a powerful sword attack: ",
                "you gain 1 Charge every %%rate%% seconds. ",
                "You can store a maximum of %%charges%% Charges. ",
                "",
                "For your next attack, your damage is ",
                "increased by the number of your Charges, ",
                "and your Charges are reset to 0. ",
                "",
                "This only applies to Swords."
        ));
        addDescArg("rate", () ->  5 - level);
        addDescArg("charges", () -> charges);
    }


    public void start() {
        if (getPlayer() != null) TimeHandler.repeatedTimeSeconds(5 - level, 0L, this);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if (event.getCause() != Cause.MELEE) return;
        if (event.getAttacker() == getPlayer() && getItemType(getPlayer().getItemInHand()) == ItemType.SWORD) {
            event.setModified(true);
            event.addSource(this);
            event.setDamage(event.getDamage() + charges);
            charges = 0;
        }
    }

    @Override
    public void addCharge() {
        if (getCurrentCharges() < getMaxCharges()) {
            charges++;
            this.getPlayer().sendMessage("Swordsmanship Charges: " + charges);
        }
    }

    @Override
    public int getCurrentCharges() {
        return charges;
    }

    @Override
    public int getMaxCharges() {
        return level + 1;
    }

    @Override
    public void task() {
        addCharge();
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }
}
