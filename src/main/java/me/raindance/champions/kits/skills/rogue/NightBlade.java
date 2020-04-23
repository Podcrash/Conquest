package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

/**
 *Night Blade
 * Class: Rogue
 * Type: Secondary Passive
 * Cooldown: 13 seconds
 * Description: Your next melee attack inflicts Blindness for 2 seconds. Night Blade then goes on cooldown.
 * Details:
 * When activated, makes a blaze sound, similar to how the sound is for preparing a bow ability.
 */
@SkillMetadata(id = 606, skillType = SkillType.Rogue, invType = InvType.SECONDARY_PASSIVE)
public class NightBlade extends Passive implements ICooldown {

    @Override
    public float getCooldown() {
        return 5;
    }

    @Override
    public String getName() {
        return "Night Blade";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void damage(DamageApplyEvent event) {
        if(onCooldown()) return;
        if(event.getAttacker() != getPlayer() || isAlly(event.getVictim())) return;
        if(event.getCause() != Cause.MELEE && event.getCause() != Cause.MELEESKILL) return;
        getPlayer().sendMessage(getUsedMessage(event.getVictim()));
        setLastUsed(System.currentTimeMillis());
        event.addSource(this);
        StatusApplier.getOrNew(event.getVictim()).applyStatus(Status.BLIND, 3, 1);
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.blaze.breathe", 0.75f, 200);
        event.setModified(true);

    }

}
