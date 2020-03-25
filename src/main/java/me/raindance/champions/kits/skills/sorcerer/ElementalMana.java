package me.raindance.champions.kits.skills.sorcerer;

import com.podcrash.api.mc.events.DeathApplyEvent;
import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

/**
 * This is a placeholder, this doesn't actually do anything
 */
@SkillMetadata(id = 1002, skillType = SkillType.Sorcerer, invType = InvType.INNATE)
public class ElementalMana extends Passive {
    @Override
    public String getName() {
        return "Elemental Mana";
    }


    @EventHandler
    public void kill(DeathApplyEvent event) {
        if(event.getAttacker() != getPlayer()) return;
        EnergyBar eBar = getChampionsPlayer().getEnergyBar();
        eBar.incrementEnergy(50);
    }
}
