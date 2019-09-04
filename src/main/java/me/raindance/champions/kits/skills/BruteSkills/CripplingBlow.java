package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

public class CripplingBlow extends Passive { // Crippling Blow is a Passive skill
    private final int SKILL_TOKEN_WEIGHT = 2; // It costs two skill tokens
    private final int MAX_LEVEL = 1; // But only has one level

    public CripplingBlow(Player player, int level) {
        super(player, "Crippling Blow", level, SkillType.Brute, InvType.PASSIVEB);
        setDesc(Arrays.asList(
                "Your powerful axe attacks give ",
                "targets Slow 2 for 1.5 seconds, ",
                "as well as 25% less knockback."
        ));
    } // It has a name, level, description, class it belongs to and it's a Passive B

    @Override
    public int getSkillTokenWeight() {
        return SKILL_TOKEN_WEIGHT;
    } // The default is one token per level so I need this to get two

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    } // It has a maximum level so you can't paralyse people for life with Crippling Blow 12

    @EventHandler(
            priority = EventPriority.LOW
    ) // The priority is monitor because, the Brute wants to watch you suffer rather than end your pain immediately

    public void onHit(DamageApplyEvent event) { // When you smack someone it does stuff
        if (event.isCancelled()) return; // Something about non-players
        if (getPlayer() == event.getAttacker() && getItemType(getPlayer().getItemInHand()) == ItemType.AXE && event.getCause() == Cause.MELEE) { // You make a note of the noob who got smacked
            if(event.getVictim() instanceof Player) {
                Player victim = (Player) event.getVictim(); // The player who took the damage is the victim
                event.setVelocityModifierX(0.75); // They take 0.75 of the knockback in the X direction
                event.setVelocityModifierZ(0.75); // They take 0.75 of the knockback in the Z direction
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 2, 1); // The victim gets crippled
                event.addSkillCause(this);
            }
        }
    }
}
