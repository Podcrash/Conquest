package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.event.EventHandler;

/**
 * Shadow Assault
 * Class: Rogue
 * Type: Primary Passive
 * Cooldown: 7 seconds
 * Description: Your next melee attack deals 3 bonus damage. Shadow Assault then goes on cooldown.
 */
@SkillMetadata(id = 608, skillType = SkillType.Rogue, invType = InvType.PASSIVEA)
public class ShadowAssault extends Passive implements ICooldown {
    @Override
    public float getCooldown() {
        return 7;
    }

    @Override
    public String getName() {
        return "Shadow Assault";
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
        event.setDamage(event.getDamage() + 3);
        event.setModified(true);

    }
}
