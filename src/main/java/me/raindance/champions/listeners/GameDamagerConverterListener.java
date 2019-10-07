package me.raindance.champions.listeners;

import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.disguise.Disguise;
import com.podcrash.api.mc.disguise.Disguiser;
import com.podcrash.api.mc.effect.status.ThrowableStatusApplier;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.classes.Assassin;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class GameDamagerConverterListener extends ListenerBase {
    private static Map<Arrow, Float> arrowDamageMap = new HashMap<>();
    private Map<String, Long> delay = new HashMap<>();

    public GameDamagerConverterListener(JavaPlugin plugin) {
        super(plugin);
    }

    public static void forceAddArrow(Arrow arrow, float charge) {
        arrowDamageMap.putIfAbsent(arrow, charge);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void damage(EntityDamageByEntityEvent event) {
        //clean();
        if (event.getEntity() instanceof Player) {
            Disguise possDisguise = Disguiser.getSeenDisguises().get(event.getEntity().getEntityId());
            if(handleDisguise(possDisguise, event)) return;
            if (event.getDamager() instanceof Projectile) {
                ChampionsPlayer cVictim = b(event.getEntity());
                ChampionsPlayer cDamager = b((Entity) ((Projectile) event.getDamager()).getShooter());
                if(!(event.getDamager() instanceof Arrow)) return;
                if(System.currentTimeMillis() < delay.getOrDefault(event.getEntity().getName(), 0L))
                    return;
                Arrow arrow = (Arrow) event.getDamager();
                if (cVictim != null && cDamager != null && arrow != null && arrowDamageMap.containsKey(arrow)) {
                    event.setCancelled(true);
                    double damage = (cDamager instanceof Assassin) ? 5 * arrowDamageMap.get(arrow) : 8 * arrowDamageMap.get(arrow);
                    DamageApplier.damage((LivingEntity) event.getEntity(), (Player) ((Projectile) event.getDamager()).getShooter(), damage, arrow, true);
                    delay.put(event.getEntity().getName(), System.currentTimeMillis() + 100);
                    arrow.remove();
                }
                ThrowableStatusApplier.apply(arrow, event.getEntity());
            }
        }
        clean();
    }

    /**
     * Pass arrows to the original user
     */
    private boolean handleDisguise(Disguise possDisguise, EntityDamageByEntityEvent event) {
        if(possDisguise != null) {
            if(possDisguise.getEntity() instanceof Player) {
                ChampionsPlayer cVictim = b(possDisguise.getEntity());
                ChampionsPlayer cDamager = b((Entity) ((Projectile) event.getDamager()).getShooter());
                if(!(event.getDamager() instanceof Arrow)) return false;
                Arrow arrow = (Arrow) event.getDamager();
                if (cVictim != null && cDamager != null && arrow != null && arrowDamageMap.containsKey(arrow)) {
                    event.setCancelled(true);
                    double damage = (cDamager instanceof Assassin) ? 5 * arrowDamageMap.get(arrow) : 8 * arrowDamageMap.get(arrow);
                    DamageApplier.damage((LivingEntity) event.getEntity(), (Player) ((Projectile) event.getDamager()).getShooter(), damage, arrow, true);

                }
                ThrowableStatusApplier.apply(arrow, event.getEntity());
                arrow.remove();
                return true;
            }else ((LivingEntity) possDisguise.getEntity()).damage(event.getDamage(), event.getDamager());
        }
        return false;
    }

    /**
     * Cancel shooting in water
     * @param event
     */
    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void shootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow) {
            if(event.getEntity().getLocation().getBlock().getType() == Material.WATER ||
                    event.getEntity().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                event.setCancelled(true);
                return;
            }
            float force = event.getForce();
            Arrow arrow = (Arrow) event.getProjectile();
            arrowDamageMap.put(arrow, force);
        }
    }

    /**
     * Remove arrows quickly
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void arrowLand(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Arrow) {
            event.getEntity().remove();
        }
    }

    private void clean() {
        arrowDamageMap.keySet().removeIf(arrow -> arrow.isDead() || !arrow.isValid());
    }


    /**
     * Easy way to get a champions player
     * @param entity
     * @return
     */
    private ChampionsPlayer b(Entity entity) {
        if (!(entity instanceof Player)) return null;
        if (entity != null) {
            Player player = (Player) entity;
            ChampionsPlayerManager cManager = ChampionsPlayerManager.getInstance();
            ChampionsPlayer cPlayer = cManager.getChampionsPlayer(player);
            if (cPlayer != null) {
                return cPlayer;
            }
        }
        return null;
    }
}
