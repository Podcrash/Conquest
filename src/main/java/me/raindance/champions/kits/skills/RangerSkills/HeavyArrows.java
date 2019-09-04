package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.util.EntityUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class HeavyArrows extends Passive {
    private final int MAX_LEVEL = 3;
    private final int damage;
    private float extraV;

    public HeavyArrows(Player player, int level) {
        super(player, "Heavy Arrows", level,  SkillType.Ranger, InvType.PASSIVEB, -1);
        this.damage = 1 + level;
        extraV = 1.1f + 0.1f * level;
        setDesc(Arrays.asList(
                "Your arrows are extremely heavy, ",
                "moving 20% slower and dealing ",
                "an additional %%extraKB%%% knockback ",
                "as well as %%damage%% additional damage. ",
                "",
                "You also receive 30% reversed ",
                "velocity of your arrows while not",
                "sneaking. "
        ));
        addDescArg("extraKB", () ->  ((int)((extraV * 100) - 100) * 100.0)/100.0) ;
        addDescArg("damage", () -> damage);
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void shotArrow(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            Player player = (Player) event.getEntity();
            if (player == getPlayer()) {
                Arrow arrow = (Arrow) event.getProjectile();
                Vector newVector = arrow.getVelocity().multiply(0.8d);
                arrow.setVelocity(newVector);
                if (!getPlayer().isSneaking()) {
                    Vector fly = newVector.multiply(-0.35d).setY(newVector.getY() + 0.1d);
                    if (EntityUtil.onGround(getPlayer())) fly.setY(fly.getY() + 0.2d);
                    player.setVelocity(fly);
                    player.setFallDistance(0);
                }
            }
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void shotPlayer(DamageApplyEvent event) {
        if (event.getArrow() == null) return;
        if (event.getCause() == Cause.PROJECTILE && event.getAttacker() == getPlayer()) {
            event.setModified(true);
            event.addSkillCause(this);
            event.setDamage(event.getDamage() + damage);
            event.setVelocityModifierX(event.getVelocityModifierX() * extraV);
            event.setVelocityModifierY(event.getVelocityModifierY() * extraV);
            event.setVelocityModifierZ(event.getVelocityModifierZ() * extraV);

        }
    }
}
