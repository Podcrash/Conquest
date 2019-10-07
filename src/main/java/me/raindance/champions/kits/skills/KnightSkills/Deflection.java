package me.raindance.champions.kits.skills.KnightSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.ICharge;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.time.TimeHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class Deflection extends Passive implements ICharge, IPassiveTimer {
    private int charges = 0;
    private final int MAX_LEVEL = 3;

    public Deflection(Player player, int level) {
        super(player, "Deflection", level, SkillType.Knight, InvType.PASSIVEA);
        charges = level + 1;
        setDesc(Arrays.asList(
                "Prepare to deflect incoming attacks. ",
                "You gain 1 Charge every %%rate%% seconds. ",
                "You can store a maximum of %%charges%% Charges. ",
                "",
                "When you are attacked, the damage is ",
                "reduced by the number of your Charges, ",
                "and your Charges are reset to 0. "
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
        if (event.getVictim() == getPlayer()) {
            event.setModified(true);
            double subtract = event.getDamage() - charges;
            if (subtract < 0) {
                subtract = 0;
                charges = Math.abs((int) subtract);
            } else charges = 0;
            event.setDamage(subtract);
        }
    }

    @Override
    public void addCharge() {
        if (getCurrentCharges() < getMaxCharges()) {
            this.getPlayer().sendMessage("Deflection Charges: " + charges);
            charges++;
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
