package me.raindance.champions.kits.skills.vanguard;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
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

@SkillMetadata(skillType = SkillType.Vanguard, invType = InvType.SHOVEL)
public class Whirlwind extends Instant implements ICooldown {
    private int distance;
    private int distanceSquared;
    private int maxDamage;
    private float multiplier;
    private final static double[][] pleaseLoad = new double[60][2];

    @Override
    public float getCooldown() {
        return 13;
    }

    @Override
    public String getName() {
        return "Whirlwind";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    public Whirlwind() {
        this.distance = 5;
        this.distanceSquared = distance * distance;
        this.maxDamage = 4;
        this.multiplier = 1.7F + 0.2F * 4;

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
}
