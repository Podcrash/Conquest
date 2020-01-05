package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

@SkillMetadata(id = 804, skillType = SkillType.Vanguard, invType = InvType.DROP)
public class Guardian extends Drop implements ICooldown {
    private boolean active;
    @Override
    public String getName() {
        return "Guardian";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @Override
    public float getCooldown() {
        return 17;
    }

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        active = true;
        //TODO: ANVIL PLACED SOUND
        new GuardianProtect().run(2, 0);
        return true;
    }

    @EventHandler
    public void hit(DamageApplyEvent e) {
        if(!active) return;
        if(e.getVictim() == getPlayer()) {
            e.setDoKnockback(false);
            return;
        }
        if(!isAlly(e.getVictim()) || e.getCause() != Cause.MELEE) return;
        if(e.getVictim().getLocation().distanceSquared(getPlayer().getLocation()) >= 9) return;
        e.setDamage(.8D * e.getDamage());
        e.setModified(true);

        DamageApplier.damage(getPlayer(), e.getAttacker(), .2D * e.getDamage(), this, true);

    }

    private class GuardianProtect implements TimeResource {
        @Override
        public void task() {
            //TODO: particles
        }

        @Override
        public boolean cancel() {
            return !active || System.currentTimeMillis() - getLastUsed() >= 3L * 1000L;
        }

        @Override
        public void cleanup() {
            active = false;
        }
    }

}
