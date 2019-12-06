package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.SWORD)
public class HiltSmash extends Interaction {

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public String getName() {
        return "Hilt Smash";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    public void doSkill(Entity clickedEntity) {
        if (!onCooldown()) {
            if (clickedEntity instanceof Player) {
                Player victim = (Player) clickedEntity;
                if(!getPlayer().canSee(victim)) return;
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 4, 1);
                DamageApplier.damage(victim, getPlayer(), 5, this, false);
                this.setLastUsed(System.currentTimeMillis());
                getPlayer().sendMessage(getUsedMessage(victim));
            }
        }
    }
}
