package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import me.raindance.champions.mob.CustomEntityFirework;
import me.raindance.champions.sound.SoundPlayer;
import me.raindance.champions.util.ColorMaker;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.util.Vector;

import java.util.Arrays;

import static me.raindance.champions.world.BlockUtil.isPassable;
import static me.raindance.champions.world.BlockUtil.playerIsHere;

public class StaticLaser extends ChargeUp implements IEnergy{
    private final int MAX_LEVEL = 5;
    private double damage;
    private double range;
    private int energyUsage;
    private Player player;
    private FireworkEffect firework;


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
        if(isValid()) {
            Color color = ColorMaker.findColorViaString(getPlayer().getName());
            this.firework = FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .build();
        }
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void damage(DamageApplyEvent damage) {
        if(damage.isCancelled()) return;
        if(damage.getVictim() == player && player.isBlocking()) {
            resetCharge();
            SoundPlayer.sendSound(getPlayer(), "mob.zombie.remedy", 0.9F, 126);
        }
    }
    @Override
    public void charge() {
        super.charge();
        if(getCharge() <= 0.99) {
            useEnergy(getEnergyUsageTicks());
        }
    }

    public void release(){
        Location cur = player.getEyeLocation();
        Vector inc = cur.getDirection().normalize();
        cur.add(inc);

        Location endLoc = null;
        for(int i = 0; i < range; i += 1) {
            if(isPassable(cur.getBlock())  && playerIsHere(cur, getPlayers()) == null) {
                WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(cur.clone().add(0, 1, 0).toVector(), EnumWrappers.Particle.FIREWORKS_SPARK, 5, 0,0,0);
                player.getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
                cur.add(inc);
            } else {
                endLoc = cur;
                break;
            }
        }
        burst(endLoc);
        resetCharge();
    }

    private void burst(Location endLoc) {
        if(endLoc == null) return;
        CustomEntityFirework.spawn(endLoc, firework, getPlayers());
        SoundPlayer.sendSound(endLoc, "fireworks.launch", 1F, 63);
        SoundPlayer.sendSound(getPlayer().getLocation(), "fireworks.launch", 1F, 63);
        int dist = 4;
        int distS = dist * dist;
        for(Player p : BlockUtil.getPlayersInArea(endLoc, 4, getPlayers())) {
            if(isAlly(p) && p == getPlayer()) continue;
            double distanceS = p.getLocation().distanceSquared(endLoc);
            double delta = 1D - distanceS/distS;
            DamageApplier.damage(p, getPlayer(), damage * delta * getCharge(), this, false);
        }
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

}
