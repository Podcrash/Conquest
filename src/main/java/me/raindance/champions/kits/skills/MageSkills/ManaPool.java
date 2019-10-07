package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IConstruct;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.util.MathUtil;
import org.bukkit.entity.Player;

public class ManaPool extends Passive implements IConstruct {
    private float increase;
    public ManaPool(Player player, int level) {
        super(player, "Mana Pool", level, SkillType.Mage, InvType.PASSIVEC);
        this.increase = 0.15F * level;
        setDesc("Maximum energy is increased by %%increase%%%.");
        this.addDescArg("increase", () -> MathUtil.round(increase * 100D, 0));
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void afterConstruction() {
        EnergyBar energyBar = getChampionsPlayer().getEnergyBar();
        double a = energyBar.getMaxEnergy() *  (1 + this.increase);
        energyBar.setMaxEnergy(a);
    }
}
