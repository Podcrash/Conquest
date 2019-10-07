package me.raindance.champions.kits.skills.BruteSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.Main;
import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class WhirlwindAxe extends Instant {
    private int distance;
    private int distanceSquared;
    private int maxDamage;
    private float multiplier;
    private final Random random = new Random();
    private final static double[][] pleaseLoad = new double[60][2];
    public WhirlwindAxe(Player player, int level) {
        super(player, "Whirlwind Axe", level, SkillType.Brute, ItemType.AXE, InvType.AXE, 21 - (1 + level));
        this.distance = 4 + level;
        this.distanceSquared = distance * distance;
        this.maxDamage = 3 + level;
        this.multiplier = 1.7F + 0.2F * level;

        setDesc(Arrays.asList(
                "Whirl your axe around rapidly, dealing ",
                "up to %%damage%% damage to enemies within ",
                "%%range%% blocks, pulling them towards you. "
        ));
        addDescArg("damage", () ->  maxDamage);
        addDescArg("range", () -> distance);

        if(pleaseLoad[4][1] == 0 && pleaseLoad[32][0] == 0) {
            final double pp = (6D * Math.PI);
            int length = pleaseLoad.length;
            final double add = pp/length;
            for(int i = 0; i < length; i++) {
                double theta = (i * add);
                theta = (theta/pp) * distance/1.5D;
                pleaseLoad[i][0] = theta * (float) Math.cos(i);
                pleaseLoad[i][1] = theta * (float) Math.sin(i);
            }

        }
    }

    private void spiral(Location playerLocation) {
        new TimeResource() {
            private int a = 0;
            @Override
            public void task() {
                for(int i = a; i < a + 7; i++) {
                    playerLocation.getWorld().playSound(playerLocation, Sound.STEP_WOOL, 2f, 1f + (float) ((i/10D % (Math.PI / 2d)) / (Math.PI / 2)));
                }
                a++;
            }

            @Override
            public boolean cancel() {
                return a > 10;
            }

            @Override
            public void cleanup() {

            }
        }.run(0,0);

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for(int i = 0; i < pleaseLoad.length; i++) {
                double x = pleaseLoad[i][0];
                double z = pleaseLoad[i][1];
                Vector vector = new Vector(x, 0, z);
                playerLocation.add(vector);
                WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(playerLocation.toVector(),
                        EnumWrappers.Particle.FIREWORKS_SPARK, 4,
                        0.05F, 0.35F, 0.05F);
                playerLocation.subtract(vector);
                PacketUtil.syncSend(particle, getPlayers());
            }
        });

    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action)) return;
        if(onCooldown()){
            return;
        }
        Location center1 = getPlayer().getLocation();
        setLastUsed(System.currentTimeMillis());

        spiral(center1);
        for(Player player : getPlayers()) {

            if (player != getPlayer() /*|| isAlly(player)*/) {
                Location center = center1.clone();
                double diff = center.distanceSquared(player.getLocation());

                if (diff <= distanceSquared) {
                    Vector toCenter = center.subtract(player.getLocation()).toVector().normalize().add(new Vector(0F, 0.1F, 0F));
                    double percentage = diff / distanceSquared;
                    toCenter.multiply(multiplier)
                            .multiply(percentage);
                    player.setVelocity(toCenter);
                    DamageApplier.damage(player, getPlayer(), (double) maxDamage * (1D - percentage), this, false);
                }
            }
        }

    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
