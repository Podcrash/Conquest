package me.raindance.champions.kits.skills.sorcerer;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

import java.util.List;

import static com.podcrash.api.mc.world.BlockUtil.getPlayersInArea;

@SkillMetadata(id = 1007, skillType = SkillType.Sorcerer, invType = InvType.AXE)
public class Meteor extends Instant implements IEnergy, ICooldown {
    private int energyUsage;
    private int radius;
    private int duration;

    public Meteor() {
        energyUsage = 70;
        radius = 6;
        duration = 5;
    }

    @Override
    public float getCooldown() {
        return 7;
    }

    @Override
    public String getName() {
        return "Meteor";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public void doSkill(PlayerEvent event, Action action) {
        if (!rightClickCheck(action) || onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        Location loc = getPlayer().getEyeLocation().toVector().add(getPlayer().getLocation().getDirection().multiply(2))
                .toLocation(getPlayer().getWorld(), getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
        Fireball fireball = getPlayer().getWorld().spawn(loc, Fireball.class);
        fireball.setIsIncendiary(false);
        fireball.setYield(0);
        fireball.setShooter(getPlayer());
        useEnergy(energyUsage);
        this.setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "item.fireCharge.use", 0.75F, 63, getPlayers());

        getPlayer().sendMessage(getUsedMessage());
    }

    @EventHandler
    private void hit(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof Fireball) || !event.getEntity().getShooter().equals(getPlayer())) return;
        List<Player> playersAffected = getPlayersInArea(event.getEntity().getLocation(), radius, getPlayers());

        for(Player p: playersAffected) {
            Vector exp = VectorUtil.fromAtoB(event.getEntity().getLocation(), p.getLocation()).normalize();
            exp.multiply(1.25).setY(exp.getY() + 0.2);
            p.setVelocity(exp);
            if(isAlly(p)) continue;
            StatusApplier.getOrNew(p).applyStatus(Status.FIRE, duration, 5);
            double dist = p.getLocation().distanceSquared(event.getEntity().getLocation());
            double multiplier = (37D - dist) /36D;
            DamageApplier.damage(p, getPlayer(), 8 * multiplier, this, true);
        }
        //TODO: this needs refactor
    }

}
