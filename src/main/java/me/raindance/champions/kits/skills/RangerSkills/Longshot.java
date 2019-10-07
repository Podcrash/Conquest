package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Longshot extends Passive {
    private final int MAX_LEVEL = 3;
    private List<Arrow> arrows = new ArrayList<>();

    public Longshot(Player player, int level) {
        super(player, "Longshot", level,  SkillType.Ranger, InvType.PASSIVEB, 3.5F + level/2F);
        setDesc(Arrays.asList(
                "Active by default, hold sneak to not use it. ",
                "",
                "The farther your victims are from where you are,",
                "The more damage exponentially you deal."
        ));
        addDescArg("range", () ->  3);
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    protected void shotArrow(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        if (!onCooldown()) {
            if(getPlayer() != event.getEntity() && !(event.getProjectile() instanceof Arrow)) return;
            if (getPlayer().isSneaking()) return;
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIZZ, 0.5f, 2.0f);
            arrows.add((Arrow) event.getProjectile());
            this.setLastUsed(System.currentTimeMillis());
        } else this.getPlayer().sendMessage(getCooldownMessage());
    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    protected void shotPlayer(DamageApplyEvent event) {
        if (event.getArrow() == null) return;
        if(event.getCause() != Cause.PROJECTILE || event.getAttacker() != getPlayer()) return;
        Arrow arr = event.getArrow();
        event.setModified(true);
        event.setDamage(event.getDamage() - 3);
        if (!arrows.contains(arr)) return;
        Location vLocation = event.getVictim().getLocation();
        Location dLocation = getPlayer().getLocation();
        double distance = vLocation.distance(dLocation);
        event.setDamage(event.getDamage() + ((3 + level * 0.4) * .0009 * FastMath.pow(distance, 2)));
    }
}
