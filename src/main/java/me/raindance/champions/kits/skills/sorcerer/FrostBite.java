package me.raindance.champions.kits.skills.sorcerer;
/*
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Continuous;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.*;

@SkillMetadata(id = 1003, skillType = SkillType.Sorcerer, invType = InvType.SWORD)
public class FrostBite extends Continuous implements IEnergy, IConstruct, IPassiveTimer {
    private int i = 0;
    private final Random random = new Random();
    private SnowballRemover remover;
    private String NAME;
    private boolean cancel;
    private int amnt;
    private int energy;
    public FrostBite() {
        this.amnt = 4;
        this.energy = 50;
    }

    @Override
    public void start() {
        remover = new SnowballRemover();
        remover.run(1, 0);
    }

    @Override
    public void stop() {
        remover.unregister();
    }

    private class SnowballRemover implements TimeResource {
        private List<Entity> entities;

        public SnowballRemover() {
            this.entities = new ArrayList<>();
        }

        @Override
        public void task() {
            Iterator<Entity> iterator = entities.iterator();
            while(iterator.hasNext()) {
                Entity entity = iterator.next();
                if(!entity.getCustomName().equals(NAME)) continue;
                Location loc = entity.getLocation();
                if(loc.distanceSquared(getPlayer().getLocation()) <= 36) continue;
                entity.remove();
                iterator.remove();
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
    @Override
    public void afterConstruction() {
        this.NAME =  getPlayer().getName() + getName();
    }

    @Override
    public String getName() {
        return "Blizzard";
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
            useEnergy(energyUsage);
            if(i++ % 2 == 0) return;
            blizzard();
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
            double mult = 0.25 + 0.15 * 5;
            double x = (0.2 - (random.nextInt(40)/100d)) * mult;
            double y = (random.nextInt(20)/100d) * mult;
            double z = (0.2 - (random.nextInt(40)/100d)) * mult;

            snowball.setVelocity(vector.add(new Vector(x, y, z)).multiply(2));
            remover.entities.add(snowball);
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
        if(!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity victim = (LivingEntity) event.getEntity();
        if(victim == getPlayer()) {
            event.setCancelled(true);
            return;
        }
        if(event.getDamager() instanceof Snowball){
            Snowball snowball = (Snowball) event.getDamager();
            if(NAME.equalsIgnoreCase(snowball.getCustomName())){
                event.setCancelled(true);
                DamageApplier.damage(victim, getPlayer(), 0.3, this, false);
                StatusApplier.getOrNew(victim).applyStatus(Status.SLOW, 2, 1);
            }
        }
    }
}

 */