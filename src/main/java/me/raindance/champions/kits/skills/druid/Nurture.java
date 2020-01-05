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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SkillMetadata(id = 205, skillType = SkillType.Druid, invType = InvType.DROP)
public class Nurture extends TogglePassive implements IEnergy, TimeResource {
    @Override
    public void toggle() {
        run(1, 0);
    }

    @Override
    public String getName() {
        return "Nurture";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public int getEnergyUsage() {
        return 20;
    }

    @Override
    public void task() {
        getGame().consumeBukkitPlayer(this::buff);
        useEnergy(getEnergyUsageTicks());
    }

    private void buff(Player victim) {
        if(victim != getPlayer() && !isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.REGENERATION, 0, 0, false);
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
