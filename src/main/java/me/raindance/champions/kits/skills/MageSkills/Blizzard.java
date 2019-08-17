package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Continuous;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

public class Blizzard extends Continuous implements IEnergy {
    private final Random random = new Random();
    private final String NAME;
    private boolean cancel;
    private int amnt;
    private int energy;
    public Blizzard(Player player, int level) {
        super(player, "Blizzard", level,  SkillType.Mage, ItemType.SWORD, InvType.SWORD, -1);
        this.amnt = 1 + level;
        this.energy = 31 - level;
        this.NAME = (player == null) ? null : player.getName() + getName();
        setDesc(Arrays.asList(
                "Hold block to release a Blizzard. ",
                "Releases %%amount%% snowballs per wave ",
                "which push players away from you. ",
                "",
                "Energy: %%energy%% per Second"
        ));
        addDescArg("amount", () ->  amnt);
        addDescArg("energy", () -> energy);
    }


    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }

    @Override
    protected void doContinuousSkill() {
        run(1, 0);
    }

    @Override
    public void task() {
        double energyUsage = getEnergyUsageTicks();
        if(hasEnergy(energyUsage)) {
            blizzard();
            useEnergy(energyUsage);
        } else {
            cancel = true;
            getPlayer().sendMessage(getNoEnergyMessage());
        }
    }

    private void blizzard(){
        Location loc = getPlayer().getEyeLocation();
        for(int i = 0; i < amnt; i++){
            Vector vector = loc.getDirection().normalize();
            Snowball snowball = (Snowball) loc.getWorld().spawnEntity(loc.add(vector.clone().add(new Vector(random.nextFloat() * 0.25F, random.nextFloat() * 0.25F, random.nextFloat() * 0.25F))), EntityType.SNOWBALL);
            snowball.setCustomName(NAME);
            //this part is copied XD
            double mult = 0.25 + 0.15 * level;
            double x = (0.2 - (random.nextInt(40)/100d)) * mult;
            double y = (random.nextInt(20)/100d) * mult;
            double z = (0.2 - (random.nextInt(40)/100d)) * mult;

            snowball.setVelocity(vector.add(new Vector(x, y, z)).multiply(2));
        }
    }

    @Override
    public boolean cancel() {
        return cancel || !getPlayer().isBlocking();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        cancel = false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void snowballHit(EntityDamageByEntityEvent event){
        if(event.getEntity() == getPlayer()) {
            event.setCancelled(true);
            return;
        }
        if(event.getDamager() instanceof Snowball){
            Snowball snowball = (Snowball) event.getDamager();
            if(NAME.equalsIgnoreCase(snowball.getCustomName())){
                event.setCancelled(true);
                event.getEntity().setVelocity(snowball.getVelocity().multiply(0.05).add(new Vector(0, 0.1, 0)));
            }
        }
    }
}