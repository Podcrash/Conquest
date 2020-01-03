package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.entity.LivingEntity;

@SkillMetadata(skillType = SkillType.Druid, invType = InvType.SWORD)
public class Overgrowth extends Interaction implements ICooldown, IEnergy {
    @Override
    public void doSkill(LivingEntity clickedEntity) {
        if(onCooldown()) return;
        if(!isAlly(clickedEntity)) return;
        StatusApplier.getOrNew(clickedEntity).applyStatus(Status.ABSORPTION, 5, 1);
        setLastUsed(System.currentTimeMillis());
    }

    @Override
    public float getCooldown() {
        return 0;
    }

    @Override
    public int getEnergyUsage() {
        return 100;
    }

    @Override
    public String getName() {
        return "Overgrowth";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
