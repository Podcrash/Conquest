package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.util.MathUtil;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Intimidation extends Passive implements IPassiveTimer, TimeResource {
    private double range;
    public Intimidation(Player player, int level) {
        super(player, "Intimidation", level, SkillType.Brute, InvType.PASSIVEA);
        this.range = FastMath.pow(6 + level * 2, 2);
        setDesc(Arrays.asList(
                "You intimidate nearby enemies:  ",
                "The more health you have than your opponent, ",
                "the more slowed they become.",
                "",
                "All enemies within %%range%% blocks will ",
                "be affected."
        ));
        addDescArg("range", () -> MathUtil.round(Math.sqrt(range), 1));
    }


    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void start() {
        if (getPlayer() != null) run(10, 1);
    }

    @Override
    public void task() {
        SkillUseEvent event = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
        Location location = getPlayer().getLocation();
        for (Player victim : getPlayers()) {
            if(victim == getPlayer() || isAlly(victim)) continue;
            if(victim.getLocation().distanceSquared(location) <= range) {
                int slownesss = (int) ((getPlayer().getHealth() - victim.getHealth())/8D);
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 1, slownesss, false, true);
            }
        }
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }
}
