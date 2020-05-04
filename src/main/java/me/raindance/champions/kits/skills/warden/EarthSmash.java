package me.raindance.champions.kits.skills.warden;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.podcrash.api.callback.sources.CollideBeforeHitGround;
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.kits.iskilltypes.action.IConstruct;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.sound.SoundWrapper;
import com.podcrash.api.time.resources.EntityParticleResource;
import com.podcrash.api.util.EntityUtil;
import com.podcrash.api.util.VectorUtil;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

import java.util.List;

@SkillMetadata(id = 905, skillType = SkillType.Warden, invType = InvType.SWORD)
public class EarthSmash extends Instant implements ICooldown, IConstruct {
    private double normalRadius = 4;
    private double slamRadius = 5;
    private double boost = -1;
    private boolean isFalling = false;
    private CollideBeforeHitGround hitGround;

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public void afterConstruction() {
        hitGround = new CollideBeforeHitGround(getPlayer(), 1L,  0D, 0D, 0D).then(() -> {
            Location location = getPlayer().getLocation();
            List<LivingEntity> players = location.getWorld().getLivingEntities();
            for(LivingEntity enemy : players) {
                if(getPlayer() == enemy) continue;
                double dist = location.distanceSquared(enemy.getLocation());
                if(dist > Math.pow(slamRadius, 2)) continue;
                pound(location, enemy, 1.33333D - ((16D - dist)/16D), 8);
            }
            ParticleGenerator.generateRangeParticles(location, slamRadius, true, (int) slamRadius);
            isFalling = false;
            getPlayer().sendMessage(getUsedMessage());
        });
    }

    @Override
    public void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());

        if(!EntityUtil.onGround(getPlayer())) {
            isFalling = true;
            slamDown();
        } else {
            Location location = getPlayer().getLocation();
            List<LivingEntity> players = location.getWorld().getLivingEntities();
            for(LivingEntity enemy : players) {
                if(getPlayer() == enemy) continue;
                double dist = location.distanceSquared(enemy.getLocation());
                if(dist > Math.pow(normalRadius, 2)) continue;
                pound(location, enemy, 1.33333D - ((16D - dist)/16D), 5);
            }
            ParticleGenerator.generateRangeParticles(location, normalRadius, true, (int) normalRadius);
            setLastUsed(System.currentTimeMillis());
            getPlayer().sendMessage(getUsedMessage());
        }
    }

    private void pound(Location currentLoc, LivingEntity entity, double multiplier, double maxDamage) {
        if(multiplier > 1) multiplier = 1;
        if(!isAlly(entity)) DamageApplier.damage(entity, getPlayer(), multiplier * maxDamage, this, false);
        Vector vector = VectorUtil.fromAtoB(currentLoc, entity.getLocation()).normalize();
        vector.multiply(multiplier * 1.25D).setY(vector.getY() + 1);
        if(vector.getY() > 1D) vector.setY(1D);
        entity.setVelocity(vector);
    }

    private void slamDown() {
        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(EnumWrappers.Particle.EXPLOSION_NORMAL, 1);
        AftershockParticleResource resource = new AftershockParticleResource(getPlayer(), particles, null);
        resource.run(1);

        SoundPlayer.sendSound(getPlayer().getLocation(), "random.fizz", 1f, 126, getPlayers());
        getPlayer().setVelocity(getPlayer().getVelocity().setY(boost));
        hitGround.run();
    }

    @Override
    public String getName() {
        return "Earth Smash";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    private class AftershockParticleResource extends EntityParticleResource {
        private AftershockParticleResource(Entity entity, WrapperPlayServerWorldParticles packet, SoundWrapper sound) {
            super(entity, packet, sound);
        }

        @Override
        public boolean cancel() {
            return !isFalling || getGame().isRespawning(getPlayer());
        }
    }
}
