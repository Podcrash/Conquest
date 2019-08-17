package me.raindance.champions.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerEntityStatus;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.disguise.Disguise;
import me.raindance.champions.disguise.Disguiser;
import me.raindance.champions.effect.status.ThrowableStatusApplier;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.classes.Assassin;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class GameDamagerConverterListener extends ListenerBase {
    private static HashMap<Arrow, Float> arrowDamageMap = new HashMap<>();

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
                Arrow arrow = (Arrow) event.getDamager();
                if (cVictim != null && cDamager != null && arrow != null && arrowDamageMap.containsKey(arrow)) {
                    event.setCancelled(true);
                    double damage = (cDamager instanceof Assassin) ? 5 * arrowDamageMap.get(arrow) : 8 * arrowDamageMap.get(arrow);
                    DamageApplier.damage((LivingEntity) event.getEntity(), (Player) ((Projectile) event.getDamager()).getShooter(), damage, arrow, true);
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
     * Remove the default damage tick which makes alters your velocity
     * @param e
     */
    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void statusDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                Player p = (Player) e.getEntity();
                EntityPlayer epvictim = ((CraftPlayer) p).getHandle();
                e.setCancelled(true);
                double health = p.getHealth() - e.getDamage();
                p.setHealth((health < 0) ? 0 : health);
                WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
                packet.setEntityId(epvictim.getId());
                packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);
                p.getWorld().getPlayers().forEach(packet::sendPacket);
            }
        }
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
