package me.raindance.champions.kits.skills.marksman;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.BowShotSkill;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.PacketUtil;
import net.jafama.FastMath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

@SkillMetadata(skillType = SkillType.Marksman, invType = InvType.BOW)
public class ExplosiveBolt extends BowShotSkill implements ICooldown {
    private double range;
    private final WrapperPlayServerWorldParticles particle;
    private WrapperPlayServerWorldParticles explosion;

    @Override
    public float getCooldown() {
        return 18;
    }

    @Override
    public String getName() {
        return "Explosive Bolt";
    }

    public ExplosiveBolt() {
        super();
        this.range = FastMath.pow(5.5D, 2D);
        this.particle = ParticleGenerator.createParticle(null, EnumWrappers.Particle.EXPLOSION_NORMAL, 2, 0,0,0);

        /*
        this.explosion = new WrapperPlayServerExplosion();
        this.explosion.setRadius((float) Math.sqrt(this.range));
        explosion.setPlayerVelocityX(0);
        explosion.setPlayerVelocityY(0);
        explosion.setPlayerVelocityZ(0);
        */
        this.explosion = ParticleGenerator.createParticle(null, EnumWrappers.Particle.EXPLOSION_HUGE, 1, 0,0,0);
    }

    @Override
    protected void shotArrow(Arrow arrow, float force) {
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> ParticleGenerator.generateProjectile(arrow, particle), 1L);
    }

    @Override
    protected void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force) {
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(Main.instance, () -> explode(arrow, arrow.getLocation().getBlock().getLocation()));

    }

    @Override
    protected void shotGround(Player shooter, Location location, Arrow arrow, float force) {
        explode(arrow, location.getBlock().getLocation());
    }
    private void explode(Arrow arrow, Location location) {
        explosion.setLocation(location);
        PacketUtil.syncSend(explosion, getPlayers());
        for(final Player player : getPlayers()) {
            double deltaDistance = this.range - location.distanceSquared(player.getLocation());
            if(deltaDistance > 0) {
                double divide = deltaDistance/range;
                Vector vector = player.getLocation().add(0, 1, 0).subtract(location).toVector().normalize().multiply(0.45D + 0.6D * divide);
                vector.setY(vector.getY() + 0.3D + 0.3D * divide);
                if(vector.getY() > 0.8D) vector.setY(0.8D);
                if(EntityUtil.onGround(player)) {
                    vector.setY(vector.getY() + 0.1);
                }
                player.setVelocity(vector);
            }
            SoundPlayer.sendSound(location, "random.explode", 0.9F, 70);
        }
    }
}
