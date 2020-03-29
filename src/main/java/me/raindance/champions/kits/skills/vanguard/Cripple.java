package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

@SkillMetadata(id = 802, skillType = SkillType.Vanguard, invType = InvType.PASSIVEA)
public class Cripple extends Passive { // Crippling Blow is a Passive skill
    public Cripple() {
        super();
    }

    @Override
    public String getName() {
        return "Cripple";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.LOW) // The priority is monitor because, the Brute wants to watch you suffer rather than end your pain immediately
    public void onHit(DamageApplyEvent event) { // When you smack someone it does stuff
        if (event.isCancelled() || (getPlayer() != event.getAttacker() || event.getCause() != Cause.MELEE)) return; // Something about non-players
        if (!(event.getVictim() instanceof Player)) return;
        Player victim = (Player) event.getVictim(); // The player who took the damage is the victim
        event.setVelocityModifierX(0.3); // They take 0.1 of the knockback in the X direction
        event.setVelocityModifierZ(0.3); // They take 0.1 of the knockback in the Z direction
        StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 2, 1); // The victim gets crippled
        event.addSource(this);
    }
}
