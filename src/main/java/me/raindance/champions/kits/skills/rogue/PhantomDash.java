package me.raindance.champions.kits.skills.rogue;

import com.podcrash.api.mc.damage.DamageApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

/**
 * Phantom Dash
 * Class: Rogue
 * Type: Axe Ability
 * Cooldown: 10 seconds
 * Description: Right click your axe to activate. Launch a Phantom Pearl at target direction.
 * If it hits a player, you teleport to them, dealing 5 damage if they are an enemy.
 * Details:
 * Phantom Pearls are ender pearls.
 * The pearl emits the particles of an ender pearl landing when it lands.
 */

@SkillMetadata(id = 607, skillType = SkillType.Rogue, invType = InvType.AXE)
public class PhantomDash extends Instant implements ICooldown {
    //We are using the instant base class to format this

    //we are going to be storing our own instance of the pearl here to reference it in another event.
    private Projectile pearl; //nevermind

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        //if the action isn't right click or if there is on cooldowm
        if(!rightClickCheck(action) || onCooldown()) return;

        //Set the cooldown
        setLastUsed(System.currentTimeMillis());

        //Get the direction of the player because we need to launch projectile
        Location currentLocOfPlayer = getPlayer().getLocation();

        Vector direction = currentLocOfPlayer.getDirection();

        //we will also be assuming that the pearl doesn't have a velocity when it spawns.

        Vector mulitplied = direction.multiply(2.4F); //magic number

        //spawn the enderpearl, we may need custom of these classes but for now this is fine.
        this.pearl = getPlayer().launchProjectile(EnderPearl.class, mulitplied);

        //AT THIS POINT: we have spawned the ender pearl.
    }

    @EventHandler
    public void enderPearlHit(EntityDamageByEntityEvent event) {
        //checks
        Entity damager = event.getDamager();

        //if the damager is not a pearl or the pearl we need, return.
        //if(!(damager instanceof EnderPearl) || damager != this.pearl) return;
        //actually, just do this: check if the pearl is not the damager
        if(damager != this.pearl) return;
        Entity victim = event.getEntity();// victim

        //we want to check if the damager hit is an actual living damager and not something random (like item frames)
        if(!(victim instanceof LivingEntity)) return;

        //at this point, the pearl has hit some living damager,
        //so we need to do 5 damage and have the user teleport.

        //damage
        DamageApplier.damage((LivingEntity) victim, getPlayer(), 5, this, false);

        Vector direction = getPlayer().getLocation().getDirection();

        Location endLoc = damager.getLocation();
        //save the original direction
        endLoc.setDirection(direction);
        //teleport
        getPlayer().teleport(damager.getLocation());
        this.pearl = null;
    }

    /**
     * The forums r g0d: https://bukkit.org/threads/how-to-disable-enderpearl-teleport.128993/
     */
    @EventHandler
    public void disablePearlTeleport(PlayerTeleportEvent e) {
        //we want to make sure that everyone doesn't get blocked
        if(e.getPlayer() != getPlayer()) return;

        //we also want to make sure that pearls don't get blocked if you are not using this skill
        if(e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        //cancel
        e.setCancelled(true);
    }

    //Generator methods for this
    @Override
    public float getCooldown() {
        return 7;
    }


    @Override
    public String getName() {
        return "Phantom Dash";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }
}
