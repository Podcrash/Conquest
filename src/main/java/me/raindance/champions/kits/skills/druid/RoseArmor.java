package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

@SkillMetadata(id = 209, skillType = SkillType.Druid, invType = InvType.DROP)
public class RoseArmor extends TogglePassive implements TimeResource, IEnergy {
    private final int damage = 2;


    @Override
    public int getEnergyUsage() {
        return 50;
    }

    @Override
    public void toggle() {
        if(isToggled()) {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.RESISTANCE, Integer.MAX_VALUE, 1);
            run(1, 0);
        }else {
            unregister();
            StatusApplier.getOrNew(getPlayer()).removeStatus(Status.RESISTANCE);
        }
    }

    @Override
    public void task() {
        Location location = getPlayer().getLocation();
        location.getWorld().playSound(location, Sound.STEP_GRASS, 0.3f, 0.5f);
        useEnergy(getEnergyUsageTicks());
    }

    @Override
    public boolean cancel() {
        return !isToggled() || !hasEnergy(getEnergyUsageTicks()) || getChampionsPlayer().isSilenced();
    }

    @Override
    public void cleanup() {
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.RESISTANCE);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if(!isToggled() || event.getVictim() != getPlayer()) return;
        if(event.getCause() != Cause.MELEE) return;
        DamageApplier.damage(event.getAttacker(), getPlayer(), 2, this, false);

    }

    @Override
    public String getName() {
        return "Rose Armor";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }
}
