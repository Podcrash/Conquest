package me.raindance.champions.kits.skills.druid;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 207, skillType = SkillType.Druid, invType = InvType.SHOVEL)
public class ParalyzingPollen extends Instant implements ICooldown, IEnergy {
    private Projectile projectile;
    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public int getEnergyUsage() {
        return 40;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        useEnergy();
        //Set the cooldown
        setLastUsed(System.currentTimeMillis());

        //Get the direction of the player because we need to launch projectile
        Location currentLocOfPlayer = getPlayer().getLocation();

        Vector direction = currentLocOfPlayer.getDirection();

        //we will also be assuming that the pearl doesn't have a velocity when it spawns.

        Vector mulitplied = direction.multiply(2.4F); //magic number

        //spawn the enderpearl, we may need custom of these classes but for now this is fine.
        this.projectile = getPlayer().launchProjectile(Egg.class, mulitplied);
        projectile.setShooter(getPlayer());
        WrapperPlayServerWorldEvent packet = ParticleGenerator.createBlockEffect(projectile.getLocation(), Material.FLOWER_POT.getId());
        ParticleGenerator.generateProjectile(projectile, packet);
        getPlayer().sendMessage(getUsedMessage());
    }

    @Override
    public String getName() {
        return "Paralyzing Pollen";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @EventHandler
    public void eggHit(EntityDamageByEntityEvent event) {
        //checks
        Entity damager = event.getDamager();

        if(damager != this.projectile) return;
        Entity victim = event.getEntity();// victim

        //we want to check if the damager hit is an actual living damager and not something random (like item frames)
        if(!(victim instanceof LivingEntity)) return;

        //don't allow friendly fire with this skill
        if(isAlly((LivingEntity)victim)) return;

        StatusApplier.getOrNew((LivingEntity) victim).applyStatus(Status.ROOTED, 2, 0);
        SoundPlayer.sendSound(getPlayer(), "random.successful_hit", 0.8F, 20);
        event.setCancelled(true);
        this.projectile = null;
    }
}
