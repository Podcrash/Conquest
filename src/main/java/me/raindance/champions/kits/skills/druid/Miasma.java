package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import org.bukkit.entity.Player;

@SkillMetadata(id = 203, skillType = SkillType.Druid, invType = InvType.DROP)
public class Miasma extends TogglePassive implements IEnergy, TimeResource {
    @Override
    public void toggle() {
        run(1, 0);
    }

    @Override
    public String getName() {
        return "Miasma";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public int getEnergyUsage() {
        return 40;
    }

    @Override
    public void task() {
        getGame().consumeBukkitPlayer(this::debuff);
        useEnergy(getEnergyUsageTicks());
    }

    private void debuff(Player victim) {
        if(victim == getPlayer() || isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.POISON, 2, 0, false);
        StatusApplier.getOrNew(victim).applyStatus(Status.DIZZY, 2, 0, false);
    }
    @Override
    public boolean cancel() {
        return !isToggled() || !hasEnergy(getEnergyUsageTicks());
    }

    @Override
    public void cleanup() {
        if(!hasEnergy(getEnergyUsageTicks())) {
            forceToggle();
            getPlayer().sendMessage(getToggleMessage() + '\n' + getNoEnergyMessage());
        }
    }
}
