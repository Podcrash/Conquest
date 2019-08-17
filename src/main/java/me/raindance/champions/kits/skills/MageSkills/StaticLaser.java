package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

import static me.raindance.champions.world.BlockUtil.*;

public class StaticLaser extends ChargeUp implements IEnergy{
    private final int MAX_LEVEL = 5;
    private double damage;
    private double range;
    private int energyUsage;
    private Player player;


    public StaticLaser (Player player, int level) {
        super(player, "Static Laser", level,  SkillType.Mage, ItemType.SWORD, InvType.SWORD, 9 - 0.5f * level, 0);
        this.player = player;
        this.rate = (0.4f + 0.1f * level) / 20f;
        int wholeNumberRate = 40 + 10 * level;
        this.damage = 6 + (2 * level);
        this.range = 20 + (10 * level);
        this.energyUsage =  24;
        setDesc(Arrays.asList(
                "Hold block to charge static electricity. ",
                "Release block to fire static lazer. ",
                "",
                "Charges %%rate%%% per Second.",
                "Taking damage cancels charge. ",
                "",
                "Deals %%damage%% damage and travels up to ",
                "%%range%% blocks. ",
                "",
                "Energy: %%energy%% per Second"
        ));
        addDescArg("rate", () ->  wholeNumberRate);
        addDescArg("damage", () -> damage);
        addDescArg("range", () -> range);
        addDescArg("energy", () -> energyUsage);
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public void charge() {
        super.charge();
        if(getCharge() != 1) {
            useEnergy(getEnergyUsageTicks());
        }
    }

    public void release(){
        Location cur = player.getLocation().add(0, 0.75, 0);
        Vector inc = cur.getDirection().normalize();
        cur.add(inc);

        for(int i = 0; i < range; i += 1) {
            if(isPassable(cur.getBlock())  && playerIsHere(cur, getPlayers()) == null) {
                WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(cur.clone().add(0, 1, 0), EnumWrappers.Particle.FIREWORKS_SPARK, 5, 0,0,0);
                player.getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                cur.add(inc);
            } else {
                List<Player> list = getPlayersInArea(cur, 1, null);
                for(Player p: list) {
                    if(p != getPlayer() && isAlly(p)) {
                        DamageApplier.damage(p, getPlayer(), damage * getCharge(), this, true);
                    }
                }
                break;
            }
        }
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

}
