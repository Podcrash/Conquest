package me.raindance.champions.kits.skills.RangerSkills;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class RopedArrow extends BowShotSkill {
    private final int MAX_LEVEL = 4;
    private final double multiplier;

    public RopedArrow(Player player, int level) {
        super(player, "Roped Arrow", level, SkillType.Ranger, ItemType.BOW, InvType.BOW, 9 - level, false);
        multiplier = 0.7 + level * 0.1;
        setDesc(Arrays.asList(
                "Prepare a Roped Arrow: ",
                "",
                "Your next arrow will pull you ",
                "towards it after it hits. "
        ));
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {

    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        getPlayer().sendMessage(String.format("You shot %s", victim.getName()));
        //boost(victim.getLocation(), force, arrow.getVelocity());
    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        boost(arrow.getLocation(), force, arrow.getVelocity());
    }

    private void boost(Location endpoint, float force, Vector arrowvelocity) {
        getPlayer().sendMessage(getUsedMessage());
        Location playerLoc = getPlayer().getLocation();
        force = (3 * force + 1f) / 4f;
        Vector vector = endpoint.toVector().subtract(playerLoc.toVector()).normalize().multiply(force);
        vector.setY(vector.getY() + 0.2d * multiplier);
        Vector playerV = getPlayer().getLocation().getDirection();
        if (playerV.getY() < 0) playerV.setY(0);
        vector.setY(playerV.getY() + vector.getY());
        double yMax = 0.5d + 0.52d * multiplier;
        if (vector.getY() > yMax) vector.setY(yMax);
        getPlayer().setVelocity(vector.multiply(0.3d + 1.2 * multiplier));
        getPlayer().setFallDistance(-1.5f);
    }
}
