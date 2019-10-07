package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Inferno extends Continuous implements IEnergy {
    private final List<Item> infernoItems = new ArrayList<>();
    private final int MAX_LEVEL = 5;
    private final String NAME;
    private int energyUsage;
    private double fireSpeed;
    private float duration;
    private final int damage = 1;

    public Inferno(Player player, int level) {
        super(player, "Inferno", level, SkillType.Mage, ItemType.SWORD, InvType.SWORD, -1);
        energyUsage = 34 - level;
        fireSpeed = 1.05 + (0.15 * level);
        duration = 0.3f + (0.1f * level);
        NAME = (player != null) ? "INFERNO" + getPlayer().getName() : null;
        if(player != null) new ItemClearer();
        setDesc(Arrays.asList(
                "Hold block to use Inferno: ",
                "You spray fire at %%velocity%% velocity, ",
                "igniting enemies for %%duration%% seconds. ",
                "",
                "Energy: %%energy%% per Second"
        ));
        addDescArg("velocity", () ->  fireSpeed);
        addDescArg("duration", () -> (double)((int)(duration * 100.0))/100.0);
        addDescArg("energy", () -> energyUsage);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public double getEnergyUsageTicks() {
        return getEnergyUsage() / 20d;
    }
    /*
    @Override
    public void useEnergy(double energy) {

    }
    */
    @Override
    protected void doContinuousSkill() {
        start();
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
                if(EntityUtil.onGround(item)) {
                    item.remove();
                    infernoIterator.remove();
                }
            }
        }

        @Override
        public boolean cancel() {
            return !isValid();
        }

        @Override
        public void cleanup() {

        }
    }
}
