package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

import static com.podcrash.api.mc.world.BlockUtil.getAllPlayersHere;
import static com.podcrash.api.mc.world.BlockUtil.getPlayersInArea;

public class FireBlast extends Instant implements IEnergy {
    private int energyUsage;
    private int radius;
    private int duration;

    public FireBlast(Player player, int level) {
        super(player, "Fire Blast", level, SkillType.Mage, ItemType.AXE, InvType.AXE, 13 - level);
        energyUsage = 54 - (4 * level);
        radius =  3 + (int) (0.5 * level);
        duration = 2 + (2 * level);
        setDesc(Arrays.asList(
                "Launch a fireball which explodes on impact ",
                "dealing large knockback to enemies within ",
                "%%range%% blocks range. Also ignites enemies ",
                "for up to %%duration%% seconds. ",
                "",
                "Energy: %%energy%%"
        ));
        addDescArg("range", () ->  radius);
        addDescArg("duration", () -> duration);
        addDescArg("energy", () -> energyUsage);
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public void doSkill(PlayerInteractEvent event, Action action) {
        if (rightClickCheck(action)) {
            if (!onCooldown()) {
                if(hasEnergy()) {
                    Location loc = getPlayer().getEyeLocation().toVector().add(getPlayer().getLocation().getDirection().multiply(2))
                            .toLocation(getPlayer().getWorld(), getPlayer().getLocation().getYaw(), getPlayer().getLocation().getPitch());
                    Fireball fireball = getPlayer().getWorld().spawn(loc, Fireball.class);
                    fireball.setIsIncendiary(false);
                    fireball.setYield(0);
                    fireball.setShooter(getPlayer());
                    useEnergy(energyUsage);
                    this.setLastUsed(System.currentTimeMillis());
                    SoundPlayer.sendSound(getPlayer().getLocation(), "item.fireCharge.use", 0.75F, 63, getPlayers());
                }else this.getPlayer().sendMessage(getNoEnergyMessage());
            }
        }
    }

    @EventHandler
    private void hit(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Fireball && event.getEntity().getShooter().equals(getPlayer())) {
            List<Player> playersAffected = getPlayersInArea(event.getEntity().getLocation(), radius, getPlayers());
            List<Player> dirHitPlayers = getAllPlayersHere(event.getEntity().getLocation(), getPlayers());

            for(Player p: playersAffected) {
                Vector exp = VectorUtil.fromAtoB(event.getEntity().getLocation(), p.getLocation()).normalize();
                exp.multiply(1.25).setY(exp.getY() + 0.2);
                p.setVelocity(exp);
                StatusApplier.getOrNew(p).applyStatus(Status.FIRE, duration, 1);
                if(dirHitPlayers.contains(p)) {
                    if(!isAlly(p)) {
                        DamageApplier.damage(p, getPlayer(), 4 + level, this, true);
                    }
                }
            }
            //TODO: this needs refactor
        }
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
