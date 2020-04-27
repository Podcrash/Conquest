package me.raindance.champions.kits.skills.sorcerer;

import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.util.VectorUtil;
import com.podcrash.api.world.BlockUtil;
import com.podcrash.api.kits.iskilltypes.action.IEnergy;
import com.podcrash.api.kits.skilltypes.ChargeUp;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

//@SkillMetadata(id = 1012, skillType = SkillType.Sorcerer, invType = InvType.SWORD)
public class Gust extends ChargeUp implements IEnergy {
    private final float duration = 4;           //  duration of slow

    private final int freq = 5;                 // Frequency of particles
    private final float rate = 0.05f;          // Rate of charge

    private final float cooldown = 10;
    private final int energy = 40;

    private final double userPushMult = 0.75;
    private final double userVertBoost = 0.25;

    private final double enemyPushMult = 1.5;
    private final double enemyVertBoost = 0.6;

    @Override
    public float getRate() {
        return rate;
    }

    @Override
    public void release() {
        if(isInWater()) {
            getPlayer().sendMessage(getWaterMessage());
            return;
        }
        useEnergy();
        Vector enemyPush = getPlayer().getEyeLocation().getDirection().normalize().multiply(enemyPushMult * getCharge());
        enemyPush.setY(getPlayer().getEyeLocation().getDirection().normalize().getY() + enemyVertBoost);

        if(!getPlayer().isSneaking()) {
            Vector userPush = getPlayer().getEyeLocation().getDirection().normalize().multiply(-(userPushMult * getCharge()));
            userPush.setY(userPush.getY() + userVertBoost);
            getPlayer().setVelocity(userPush);
        }

        WrapperPlayServerWorldParticles particlePlayer = ParticleGenerator.createParticle(getPlayer().getLocation().toVector(), EnumWrappers.Particle.EXPLOSION_LARGE, 1, 0,0,0);
        PacketUtil.asyncSend(particlePlayer, getPlayers());

        Location effectLocation = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().normalize().multiply(2));

        for (Player p : BlockUtil.getPlayersInArea(effectLocation, 3, getPlayers())) {
            if (p == getPlayer()) continue;
            if(!isAlly(p)) {
                StatusApplier applier = StatusApplier.getOrNew(p);
                applier.applyStatus(Status.SLOW, duration, 0, true, true);
            }
            p.setVelocity(enemyPush);
            createEffect(getPlayer().getEyeLocation(), p.getEyeLocation());
            DamageApplier.damage(p, getPlayer(), 0, this, false);
        }

        // This was the old sound, pyro wanted the bat takeoff for a new one
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.wither.shoot", 0.4F, 88);
        //SoundPlayer.sendSound(getPlayer().getLocation(), "mob.bat.takeoff", 1F, 57);

    }

    @Override
    public float getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "Gust";
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }

    private void createEffect(Location start, Location end) {
        Vector cur = start.toVector();
        Vector inc = VectorUtil.fromAtoB(start.toVector(), end.toVector()).divide(new Vector(freq, freq, freq));
        for(int i = 0; i < freq; i++) {
            WrapperPlayServerWorldParticles particle = ParticleGenerator.createParticle(cur, EnumWrappers.Particle.EXPLOSION_NORMAL, 1, 0,0,0);
            PacketUtil.asyncSend(particle, getPlayers());
            cur.add(inc);
        }

    }
}
