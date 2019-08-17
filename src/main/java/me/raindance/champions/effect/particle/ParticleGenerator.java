package me.raindance.champions.effect.particle;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerWorldEvent;
import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.sound.SoundWrapper;
import me.raindance.champions.world.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.LinkedList;
import java.util.List;

public final class ParticleGenerator {
    private ParticleGenerator() {

    }

    public static void generate(Player p, AbstractPacket... packets) {
        for (AbstractPacket packet : packets) {
            packet.sendPacket(p);
        }
    }
    public static void generate(Player p, List<AbstractPacket> packets) {
        for (int i = 0; i < packets.size(); i++) {
            packets.get(i).sendPacket(p);
        }
    }

    public static void sendToAll(Player player, AbstractPacket packet){
        for(Player p : player.getWorld().getPlayers()){
            packet.sendPacket(p);
        }

    }

    public static WrapperPlayServerWorldEvent createPotionParticle(Location loc, int potionEffectId){
        WrapperPlayServerWorldEvent playServerWorldEvent = new WrapperPlayServerWorldEvent();
        playServerWorldEvent.setEffectId(2007);
        playServerWorldEvent.setLocation(new BlockPosition(loc.toVector()));
        playServerWorldEvent.setData(potionEffectId);
        playServerWorldEvent.setDisableRelativeVolume(false);
        return playServerWorldEvent;
    }

    public static WrapperPlayServerWorldEvent createBlockEffect(Location location, int blockID) {
        WrapperPlayServerWorldEvent event = new WrapperPlayServerWorldEvent();
        event.setLocation(new BlockPosition(location.toVector()));
        event.setEffectId(2001);
        event.setData(blockID);
        return event;
    }

    public static WrapperPlayServerWorldParticles createParticle(EnumWrappers.Particle particle, int count) {
       return createParticle(null, particle, count, 0,0,0);
    }
    public static WrapperPlayServerWorldParticles createParticle(Location loc, EnumWrappers.Particle particle, int[] data, int particleCount, float offsetX, float offsetY, float offsetZ) {
        WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
        packet.setParticleType(particle);
        if (loc == null) loc = new Location(Bukkit.getWorlds().get(0), 1, 1, 1);
        packet.setX((float) loc.getX());
        packet.setY((float) loc.getY());
        packet.setZ((float) loc.getZ());
        packet.setNumberOfParticles(particleCount);
        packet.setOffsetX(offsetX);
        packet.setOffsetY(offsetY);
        packet.setOffsetZ(offsetZ);
        packet.setData(data);
        return packet;
    }
    public static WrapperPlayServerWorldParticles createParticle(Location loc, EnumWrappers.Particle particle, int particleCount, float offsetX, float offsetY, float offsetZ) {
        return createParticle(loc, particle, new int[]{1}, particleCount, offsetX, offsetY, offsetZ);
    }

    public static void generateProjectile(Projectile proj, WrapperPlayServerWorldParticles packet) {
        ParticleRunnable.particleRunnable.getWrappers().add(new ProjectileParticleWrapper(proj, packet));
    }
    public static void generateEntity(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound){
        ParticleRunnable.particleRunnable.getWrappers().add(new EntityParticleWrapper(entity, packet, sound));
    }

    //for stuff like seismic slam
    public static void generateRangeParticles(final Location center, final double radius, final boolean under) {
        double radius2 = radius / 2d;
        double startX = center.getX() - radius2;
        double startZ = center.getZ() - radius2;
        double endX = center.getX() + radius2;
        double endZ = center.getZ() + radius2;
        LinkedList<AbstractPacket> particles = new LinkedList<>();
        for (double x = startX; x <= endX; x += 1D) {
            for (double z = startZ; z <= endZ; z += 1D) {
                Location test = BlockUtil.getHighestUnderneath(new Location(center.getWorld(), x, center.getY(), z));
                WrapperPlayServerWorldParticles particle = createParticle(test, EnumWrappers.Particle.BLOCK_CRACK, new int[]{test.getBlock().getTypeId(), 0}, 15, 0.1F, 0.1F, 0.1F);
                particles.add(particle);

            }
        }
        for (Player p : center.getWorld().getPlayers()) {
            generate(p, particles);
        }
    }
}
