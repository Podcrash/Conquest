package me.raindance.champions.kits.skills.marksman;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
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

@SkillMetadata(skillType = SkillType.Marksman, invType = InvType.INNATE)
public class Longshot extends Passive implements ICooldown, IPassiveTimer, TimeResource {
    private List<Arrow> arrows = new ArrayList<>();

    //TODO: Make this its own static method within the engine
    private void updateXP() {
        if(!onCooldown()) {
            getPlayer().setLevel(0);
            getPlayer().setExp(0);
        }else {
            double cooldown = cooldown();
            int level = (int) Math.floor(cooldown);
            double decimal = cooldown - level;

            getPlayer().setLevel(level);
            getPlayer().setExp((float) decimal);
        }

    }

    @Override
    public void start() {
        run(1, 1);
    }

    @Override
    public void task() {
        updateXP();
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {
        arrows.clear();
    }

    @Override
    public float getCooldown() {
        return 3;
    }

    @Override
    public String getName() {
        return "Longshot";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.LOW)
    protected void shotArrow(EntityShootBowEvent event) {
        if (event.isCancelled()) return;
        if (onCooldown()) return;
        if(getPlayer() != event.getEntity() && !(event.getProjectile() instanceof Arrow)) return;
        if (getPlayer().isSneaking()) return;

        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.FIZZ, 0.5f, 2.0f);
        arrows.add((Arrow) event.getProjectile());
        this.setLastUsed(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOW)
    protected void shotPlayer(DamageApplyEvent event) {
        if (event.getArrow() == null) return;
        if(event.getCause() != Cause.PROJECTILE || event.getAttacker() != getPlayer()) return;
        Arrow arr = event.getArrow();
        event.setModified(true);
        event.setDamage(event.getDamage() - 1);
        if (!arrows.contains(arr)) return;
        Location vLocation = event.getVictim().getLocation();
        Location dLocation = getPlayer().getLocation();
        double distance = vLocation.distance(dLocation);
        event.setDamage(event.getDamage() + ((3.8 * .0009) * FastMath.pow(distance, 2)));
    }
}
