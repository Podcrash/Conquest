package me.raindance.champions.kits.skills.thief;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.time.TimeHandler;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;

@SkillMetadata(id = 708, skillType = SkillType.Thief, invType = InvType.SWORD)
public class ShockingStrike extends Interaction implements ICooldown {
    private String victimName;
    @Override
    public void doSkill(LivingEntity victim) {
        if(isAlly(victim)) return;

        this.victimName = victim.getName();
        StatusApplier.getOrNew(victim).applyStatus(Status.SHOCK, 5, 1);
        victim.getWorld().playSound(victim.getLocation(), Sound.BAT_HURT, 1.0f, 1.5f);
        TimeHandler.delayTime(5000, () -> {
            victimName = "";
        });
    }

    @EventHandler
    public void sprint(PlayerToggleSprintEvent event) {
        if(!event.getPlayer().getName().equalsIgnoreCase(victimName)) return;
        if(!event.isSprinting()) return;
        event.setCancelled(true);
    }

    @Override
    public float getCooldown() {
        return 10;
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
