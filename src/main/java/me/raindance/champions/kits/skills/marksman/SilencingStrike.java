package me.raindance.champions.kits.skills.marksman;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Interaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;

@SkillMetadata(skillType = SkillType.Marksman, invType = InvType.SWORD)
public class SilencingStrike extends Interaction {
    private final float duration = 2;
    private final double damage = 2;

    @Override
    public float getCooldown() {
        return 17;
    }

    @Override
    public String getName() {
        return "Silencing Strike";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    @Override
    public void doSkill(LivingEntity clickedEntity) {
        if (!onCooldown()) {
            if (clickedEntity instanceof Player) {
                Player victim = (Player) clickedEntity;
                if(!getPlayer().canSee(victim)) return;
                StatusApplier.getOrNew(victim).applyStatus(Status.SILENCE, duration, 1);
                DamageApplier.damage(victim, getPlayer(), damage, this, false);
                this.setLastUsed(System.currentTimeMillis());
                SoundPlayer.sendSound(getPlayer().getLocation(), "mob.spider.say", 0.9F, 77);
                getPlayer().sendMessage(getUsedMessage(victim));
            }
        }
    }
}