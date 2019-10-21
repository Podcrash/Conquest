package me.raindance.champions.kits.skills.MageSkills;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class NullBlade extends Passive {
    private final int MAX_LEVEL = 3;
    private double energyGain;
    public NullBlade(Player player, int level) {
        super(player, "Null Blade", level, SkillType.Mage, InvType.PASSIVEB);
        energyGain = 4 + (2 * level);
        setDesc(Arrays.asList(
                "Your attacks suck the life from ",
                "opponents, restoring %%gain%% energy. "
        ));
        addDescArg("gain", () ->  energyGain);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler
    public void hit(DamageApplyEvent event){
        if(event.isCancelled()) return;
        if (event.getAttacker() == getPlayer()) {
            EnergyBar ebar = getChampionsPlayer().getEnergyBar();
            boolean willOverfill = ebar.getEnergy() + energyGain > ebar.getMaxEnergy();
            event.addSource(this);
            if(willOverfill) {
                ebar.setEnergy(ebar.getMaxEnergy());
            } else {
                ebar.setEnergy(ebar.getEnergy() + energyGain);
            }
        }
    }
}
