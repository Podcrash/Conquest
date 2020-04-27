package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.skilltypes.Passive;
import com.podcrash.api.sound.SoundPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SkillMetadata(id = 602, skillType = SkillType.Rogue, invType = InvType.PRIMARY_PASSIVE)
public class ChainAttack extends Passive {
    private int bonus = 0;
    private long lastHit = 0;
    private String affectedPlayer;


    @EventHandler(priority = EventPriority.LOW)
    public void onHit(DamageApplyEvent event) {
        if (event.isCancelled()) return;
        // cba with non players
        if(event.getCause() != Cause.MELEE) return;
        if(event.getAttacker() != getPlayer()) return;
        if (System.currentTimeMillis() - lastHit > 2 * 1000) reset();
        event.addSource(this);
        lastHit = System.currentTimeMillis();
        LivingEntity victim = event.getVictim();
        event.setDamage(event.getDamage() + bonus);
        event.setModified(true);
        SoundPlayer.sendSound(victim.getLocation(), "note.hat", 0.9F, 110);
        if (bonus < 3) {
            if (bonus == 0 || affectedPlayer == null || affectedPlayer.equals(victim.getName())) {
                affectedPlayer = victim.getName();
                bonus++;
            } else reset();
        }
    }

    public void reset() {
        bonus = 0;
    }

    @Override
    public String getName() {
        return "Chain Attack";
    }
}
