package me.raindance.champions.kits.skills.RangerSkills;

import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.BowChargeUp;
import com.podcrash.api.mc.listeners.GameDamagerConverterListener;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class Barrage extends BowChargeUp {
    private int numArrows;
    private final double PI2 = Math.PI * 2;
    public Barrage(Player player, int level) {
        super(player, "Barrage", level,  SkillType.Ranger, InvType.PASSIVEA, -1, (0.4F + 0.1F * level)/20F);
        numArrows = 2 + 2 * level;
        setDesc(Arrays.asList(
                "Charge your bow to fire bonus arrows. ",
                "",
                "Charges %%rate%%% per Second. ",
                "",
                "Fires up to %%arrows%% additional arrows."
        ));
        addDescArg("rate", () ->  (int)((0.4F + 0.1F * level) * 100));
        addDescArg("arrows", () -> numArrows);
    }

    @Override
    public void doShoot(Arrow arrow, float charge) {
        if(charge <= 0.1F) return;
        int total = (int) (((charge * numArrows) + 0.5F)/2);
        Location location = arrow.getLocation();
        World world = location.getWorld();
        Vector vector = arrow.getVelocity();
        float speed  = (float) vector.length();
        vector = vector.clone().normalize();
        final double increment = Math.PI/total;
        for(double i = 0; i <= PI2; i += increment) {
            double x = 0.05 * Math.sin(i);
            double y = 0.04 * Math.sin(i);
            double z = 0.05 * Math.cos(i);
            Vector vector1 = new Vector(x, z, y);
            Arrow arrow1 = world.spawnArrow(location, vector.clone().add(vector1).normalize(), speed, 0);
            arrow1.setShooter(getPlayer());
            GameDamagerConverterListener.forceAddArrow(arrow1, 0.9F);
            SoundPlayer.sendSound(getPlayer().getLocation(), "random.bow", 0.12F, 70);

        }
    }

    @Override
    public void shootPlayer(Arrow arrow, float charge, DamageApplyEvent e) {

    }

    @Override
    public void shootGround(Arrow arrow, float charge) {

    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
