package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

@SkillMetadata(id = 306, skillType = SkillType.Duelist, invType = InvType.SECONDARY_PASSIVE)
public class PreemptiveStrike extends Passive implements ICooldown {

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public String getName() {
        return "Preemptive Strike";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(onCooldown() || e.getAttacker() != getPlayer()) return;
        if(isAlly(e.getVictim())) return;
        setLastUsed(System.currentTimeMillis());
        StatusApplier.getOrNew(e.getVictim()).applyStatus(Status.WEAKNESS, 4, 0);
        getPlayer().sendMessage(getUsedMessage(e.getVictim()));
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.guardian.curse", 0.75F, 90);

    }
}
