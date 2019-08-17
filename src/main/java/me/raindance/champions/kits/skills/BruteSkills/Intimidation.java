package me.raindance.champions.kits.skills.BruteSkills;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import me.raindance.champions.time.resources.TimeResource;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Intimidation extends Passive implements IPassiveTimer, TimeResource {
    private float range;
    public Intimidation(Player player, int level) {
        super(player, "Intimidation", level, SkillType.Brute, InvType.PASSIVEA);
        this.range = (float) FastMath.pow(6 + level * 2, 2);
        setDesc(Arrays.asList(
                "You intimidate nearby enemies:  ",
                "The more health you have than your opponent, ",
                "the more slowed they become.",
                "",
                "All enemies within %%range%% blocks will ",
                "be affected."
        ));
        addDescArg("range", () ->  Math.sqrt(range));
    }


    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void start() {
        if (getPlayer() != null) run(20, 1);
    }

    @Override
    public void task() {
        Location location = getPlayer().getLocation();
        for (Player victim : getPlayers()) {
            if(victim == getPlayer()) return;
            if(victim.getLocation().distanceSquared(location) <= range) {
                int slownesss = (int) ((getPlayer().getHealth() - victim.getHealth())/8D);
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 1, slownesss);
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
