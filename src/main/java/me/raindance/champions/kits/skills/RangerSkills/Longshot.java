package me.raindance.champions.kits.skills.RangerSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
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
        super(player, "Longshot", level,  SkillType.Ranger, InvType.PASSIVEB, 7 - level);
        setDesc(Arrays.asList(
                "Active by default, hold sneak to not use it. ",
                "",
                "Arrows fire 20% faster and ",
                "deal an additional 1 damage ",
                "for every %%range%% blocks they travelled.",
                "",
                "Maximum of 12 additional damage."
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
            if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
                Player player = (Player) event.getEntity();
                if (player == getPlayer() && !getPlayer().isSneaking()) {
                    Arrow arrow = (Arrow) event.getProjectile();
                    arrow.setVelocity(arrow.getVelocity().multiply(1.2d));
                    arrows.add(arrow);
                    getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIZZ, 0.5f, 2.0f);
                    this.getPlayer().sendMessage(getUsedMessage());
                    this.setLastUsed(System.currentTimeMillis());
                }
            }
        } else this.getPlayer().sendMessage(getCooldownMessage());
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    protected void shotPlayer(DamageApplyEvent event) {
        if (event.getArrow() == null) return;
        Arrow arr = event.getArrow();
        if (arrows.contains(arr) && event.getCause() == Cause.PROJECTILE && event.getAttacker() == getPlayer()) {
            Player damager = (Player) event.getAttacker();
            LivingEntity victim = event.getVictim();
            Location vLocation = victim.getLocation();
            Location dLocation = damager.getLocation();
            double distance = vLocation.distance(dLocation);
            event.setModified(true);
            event.setDamage(event.getDamage() + (distance / 3));
        }
    }
}
