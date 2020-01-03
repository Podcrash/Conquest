package me.raindance.champions.kits.skills.duelist;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.PASSIVEB)
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
        setLastUsed(System.currentTimeMillis());
        StatusApplier.getOrNew(e.getVictim()).applyStatus(Status.WEAKNESS, 4, 0);
        getPlayer().sendMessage(getUsedMessage(e.getVictim()));
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.guardian.curse", 0.75F, 90);

    }
}
