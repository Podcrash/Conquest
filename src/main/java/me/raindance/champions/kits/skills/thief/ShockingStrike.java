package me.raindance.champions.kits.skills.thief;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Interaction;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

@SkillMetadata(id = 708, skillType = SkillType.Thief, invType = InvType.SWORD)
public class ShockingStrike extends Interaction implements ICooldown {

    private int duration = 4;

    @Override
    public void doSkill(LivingEntity victim) {
        if(isAlly(victim)) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.SHOCK, duration, 1);
        StatusApplier.getOrNew(victim).applyStatus(Status.GROUND, duration, 1);
        StatusApplier.getOrNew(victim).applyStatus(Status.SILENCE, duration, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BAT_HURT, 1.0f, 1.5f);

        setLastUsed(System.currentTimeMillis());

        landed();
    }

    @Override
    public float getCooldown() {
        return 7;
    }

    @Override
    public String getName() {
        return "Shocking Strike";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
