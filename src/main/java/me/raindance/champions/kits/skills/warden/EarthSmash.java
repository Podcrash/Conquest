package me.raindance.champions.kits.skills.warden;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.util.VectorUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

@SkillMetadata(skillType = SkillType.Warden, invType = InvType.SWORD)
public class EarthSmash extends Instant implements ICooldown {
    @Override
    public float getCooldown() {
        return 16;
    }

    @EventHandler
    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) && onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        Location location = getPlayer().getLocation();
        List<Player> players = getPlayers();
        for(Player enemy : players) {
            if(isAlly(enemy) || getPlayer() == enemy) continue;
            double dist = location.distanceSquared(enemy.getLocation());
            if(dist > 16) continue;
            pound(location, enemy, dist/16D);
        }
        ParticleGenerator.generateRangeParticles(location, 8, true);
    }

    private void pound(Location currentLoc, LivingEntity entity, double multiplier) {
        DamageApplier.damage(entity, getPlayer(), multiplier * 5D, this, false);
        Vector vector = VectorUtil.fromAtoB(currentLoc, entity.getLocation());
        vector.setY(vector.getY() + 0.5D)
            .multiply(multiplier);
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
