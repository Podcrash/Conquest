package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.events.DeathApplyEvent;
import com.podcrash.api.time.resources.TimeResource;
import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 105, skillType = SkillType.Berserker, invType = InvType.INNATE)
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
        lastHit = System.currentTimeMillis() - 3000;
        run(1, 0);
    }

    @Override
    public void task() {
        if(lastHit != 0 && System.currentTimeMillis() - lastHit >= 3000) {
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
        if(e.getAttacker() != getPlayer() || isAlly(e.getVictim())) return;
        EnergyBar energyBar = getEnergyBar();

        lastHit = System.currentTimeMillis();

        double currentEnergy = energyBar.getEnergy();
        if(currentEnergy >= 4) getChampionsPlayer().heal(1);
        else energyBar.incrementEnergy(1);
    }

    @EventHandler
    public void die(DeathApplyEvent e) {
        if(e.getPlayer() != getPlayer()) return;
        getEnergyBar().incrementEnergy(-getEnergyBar().getMaxEnergy());
    }

    protected EnergyBar getEnergyBar() {
        return getChampionsPlayer().getEnergyBar();
    }


}
