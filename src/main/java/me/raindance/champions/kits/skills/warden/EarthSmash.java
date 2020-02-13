package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.VectorUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

import java.util.List;

@SkillMetadata(id = 905, skillType = SkillType.Warden, invType = InvType.SWORD)
public class EarthSmash extends Instant implements ICooldown {
    @Override
    public float getCooldown() {
        return 16;
    }

    @Override
    public void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        if(!EntityUtil.onGround(getPlayer())) return;
        setLastUsed(System.currentTimeMillis());
        Location location = getPlayer().getLocation();
        List<LivingEntity> players = location.getWorld().getLivingEntities();
        for(LivingEntity enemy : players) {
            if(isAlly(enemy) || getPlayer() == enemy) continue;
            double dist = location.distanceSquared(enemy.getLocation());
            if(dist > 16) continue;
            pound(location, enemy, 1.1D - ((16D - dist)/16D));
        }
        ParticleGenerator.generateRangeParticles(location, 4.5, true);
    }

    private void pound(Location currentLoc, LivingEntity entity, double multiplier) {
        DamageApplier.damage(entity, getPlayer(), multiplier * (5D/1.1D), this, false);
        Vector vector = VectorUtil.fromAtoB(currentLoc, entity.getLocation());
        vector.multiply(multiplier * 1.6D).setY(vector.getY() + 0.5D);
        if(vector.getY() > 0.5D) vector.setY(0.5D);
        entity.setVelocity(vector);
    }

    @Override
    public String getName() {
        return "Earth Smash";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
