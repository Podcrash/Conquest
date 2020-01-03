package me.raindance.champions.kits;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class EnergyBar implements TimeResource {
    private double energy;
    private double MAX_ENERGY;
    private String ownerName;
    private double lastTimeUsed;
    private boolean cancel = false;
    private boolean enabled = true;
    private double naturalRegenRate;

    public EnergyBar(Player p1, double eMax) {
        ownerName = p1.getName();
        MAX_ENERGY = eMax;
        setEnergy(MAX_ENERGY);
        this.naturalRegenRate = 0.5D;
        TimeHandler.repeatedTime(1,0, this);
    }

    // this is called whenever the player switches kits, and essentially cleans things up by removing the xp bar and canceling the mana regen
    public void stop() {
        getPlayer().setExp(0);
        cancel = true;
    }

    // getters and setters
    public void setEnergy(double energy) {
        double finalEnergy = (energy / MAX_ENERGY);
        if(finalEnergy > MAX_ENERGY) finalEnergy = MAX_ENERGY;

        float xp = (finalEnergy >= 1D) ? 0.99999F : (float) finalEnergy;
        getPlayer().setExp(xp);
        this.energy = energy;
        lastTimeUsed = System.currentTimeMillis();
    }

    public void setMaxEnergy(double MAX_ENERGY) {
        this.MAX_ENERGY = MAX_ENERGY;
        setEnergy(MAX_ENERGY);
    }

    public double getEnergy()
    {
        return energy;
    }
    public double getMaxEnergy() {
        return MAX_ENERGY;
    }

    public void toggleRegen(boolean bool) {
        enabled = bool;
    }

    public double getNaturalRegenRate() {
        return naturalRegenRate;
    }

    public void setNaturalRegenRate(double naturalRegenRate) {
        this.naturalRegenRate = naturalRegenRate;
    }

    // timeHandler methods
    public void task() {
        if(System.currentTimeMillis() - lastTimeUsed >= 20 && energy <= MAX_ENERGY && enabled)
            setEnergy(energy + naturalRegenRate);
    }

    public boolean cancel() {
        return cancel;
    }

    public void cleanup(){}
    
    private Player getPlayer() {
        return Bukkit.getPlayer(ownerName);
    }
}
