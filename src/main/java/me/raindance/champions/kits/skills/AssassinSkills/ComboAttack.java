package me.raindance.champions.kits.skills.AssassinSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class ComboAttack extends Passive {
    private final int MAX_LEVEL = 3;
    private int bonus = 0;
    private long lastHit = 0;
    private String affectedPlayer;

    public ComboAttack(Player player, int level) {
        super(player, "Combo Attack", level, SkillType.Assassin, InvType.PASSIVEB);
        setDesc("Each time you attack a specific opponent, your melee damage",
                "against them increases by 1, up to a maximum of %%level%%",
                "",
                "Your bonus damage is cleared after 2 seconds of not hitting them.",
                "",
                "Hitting another opponent will reset the bonus.");

        addDescArg("level", () -> this.level);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHit(DamageApplyEvent event) {
        if (event.isCancelled()) return;
        // cba with non players
        if(event.getCause() != Cause.MELEE) return;
        if(event.getAttacker() != getPlayer()) return;
        if (System.currentTimeMillis() - lastHit > 2 * 1000) reset();
        event.addSkillCause(this);
        lastHit = System.currentTimeMillis();
        LivingEntity victim = event.getVictim();
        event.setModified(true);
        event.setDamage(event.getDamage() + bonus);
        SoundPlayer.sendSound(victim.getLocation(), "note.hat", 0.9F, 110);
        if (bonus < getLevel()) {
            if (bonus == 0 || affectedPlayer == null || affectedPlayer.equals(victim.getName())) {
                affectedPlayer = victim.getName();
                bonus++;
            } else reset();
        }
    }

    public void reset() {
        bonus = 0;
    }
}
