package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Berserker, invType = InvType.INNATE)
public class Fury extends Passive implements IPassiveTimer, TimeResource {
    private long lastHit;
    @Override
    public String getName() {
        return "Fury";
    }


    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public void start() {
        run(10, 20);
    }

    @Override
    public void task() {
        if(lastHit != 0 && System.currentTimeMillis() - lastHit > 3000) {
            lastHit = 0;
            getEnergyBar().setEnergy(0);
        }
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(e.getAttacker() != getPlayer()) return;
        EnergyBar energyBar = getEnergyBar();

        energyBar.setEnergy(energyBar.getEnergy() + 1);
        lastHit = System.currentTimeMillis();

        if(energyBar.getEnergy() >= 4) {
            getChampionsPlayer().heal(1);
        }
    }

    @EventHandler
    public void die(DeathApplyEvent e) {
        if(e.getAttacker() != getPlayer()) return;
        getEnergyBar().setEnergy(0);
    }

    protected EnergyBar getEnergyBar() {
        return getChampionsPlayer().getEnergyBar();
    }


}
