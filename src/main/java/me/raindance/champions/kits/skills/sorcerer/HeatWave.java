package me.raindance.champions.kits.skills.sorcerer;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Continuous;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SkillMetadata(id = 1005, skillType = SkillType.Sorcerer, invType = InvType.SWORD)
public class HeatWave extends Continuous implements IEnergy, IConstruct, IPassiveTimer {
    private final List<Item> infernoItems = new ArrayList<>();
    private String NAME;
    private int energyUsage;
    private double fireSpeed;
    private float duration;
    private final int damage = 1;

    private ItemClearer clearer;

    public HeatWave() {
        energyUsage = 30;
        fireSpeed = 1.05 + (0.15 * 4);
        duration = 5;
    }

    @Override
    public void afterConstruction() {
        NAME = getName() + getPlayer().getName();
    }

    @Override
    public void start() {
        clearer = new ItemClearer();
    }


    @Override
    public void stop() {
        clearer.unregister();
    }

    @Override
    public String getName() {
        return "Heat Wave";
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    /*
    @Override
    public void useEnergy(double energy) {

    }
    */
    @Override
    protected void doContinuousSkill() {
        startContinuousAction();
    }

    @Override
    public void task() {
        if (hasEnergy(getEnergyUsageTicks())) {
            shootFire();
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.GHAST_FIREBALL, 0.1f, 1f);
            useEnergy(getEnergyUsageTicks());
        } else this.getPlayer().sendMessage(getNoEnergyMessage());
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking();
    }

    private void shootFire() {
        // Vector dir sets the direction and speed of the fire
        Vector dir = getPlayer().getLocation().getDirection().normalize();
        // Location spawnLoc SHOULD be the place where the fire is spawning from
        Location spawnLoc = getPlayer().getEyeLocation().add(dir);
        dir.multiply(fireSpeed);

        Item blazePowder = ItemManipulationManager.intercept(getPlayer(), Material.BLAZE_POWDER, spawnLoc, dir,
                (item, entity) -> {
                    //makes sure that you hit a player and that it wasnt yourself
                    if(entity == null) return; //null check is required because... reasons?
                    if(entity instanceof Player && !entity.equals(getPlayer())) {
                        //deals damage to enemy players and catches them on fire
                        if(!isAlly(((Player)entity)) && !BlockUtil.isInWater(entity)) {
                            StatusApplier.getOrNew((Player) entity).applyStatus(Status.FIRE, duration, 1);
                            DamageApplier.damage(entity, getPlayer(), damage, this, false);
                        }
                        //removes the blaze powder after hitting any player
                        item.remove();
                    }
                });
        blazePowder.setCustomName("RITB");
        ItemMeta meta = blazePowder.getItemStack().getItemMeta();
        meta.setDisplayName(NAME + Long.toString(System.currentTimeMillis()));
        blazePowder.getItemStack().setItemMeta(meta); //set the names are so that they don't stack
        infernoItems.add(blazePowder);
        //starts a timehandler for each blazepowder that removes it once it hits the ground
    }

    private class ItemClearer implements TimeResource {
        public ItemClearer() {
            this.run(1,0);
        }
        @Override
        public void task() {
            Iterator<Item> infernoIterator = infernoItems.iterator();
            while(infernoIterator.hasNext()) {
                Item item = infernoIterator.next();
                if(!EntityUtil.onGround(item) || item.getLocation().distanceSquared(getPlayer().getLocation()) <= 64) continue;
                item.remove();
                infernoIterator.remove();
            }
        }

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public void cleanup() {

        }
    }
}
