package me.raindance.champions.kits;
import me.raindance.champions.time.TimeHandler;
import me.raindance.champions.time.resources.TimeResource;
import org.bukkit.entity.Player;


public class EnergyBar implements TimeResource {
    private double energy;
    private double MAX_ENERGY;
    private Player owner;
    private double lastTimeUsed;
    private boolean cancel = false;
    private boolean enabled = true;

    public EnergyBar(Player p1, double eMax) {
        owner = p1;
        MAX_ENERGY = eMax;
        setEnergy(MAX_ENERGY);
        TimeHandler.repeatedTime(1,0, this);
    }

    // this is called whenever the player switches kits, and essentially cleans things up by removing the xp bar and canceling the mana regen
    public void stop() {
        owner.setExp(0);
        cancel = true;
    }

    // getters and setters
    public void setEnergy(double energy) {
        owner.setExp((float)(energy / MAX_ENERGY));
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

    // timeHandler methods
    public void task() {
        if(System.currentTimeMillis() - lastTimeUsed >= 20 && energy <= MAX_ENERGY && enabled)
            setEnergy(energy + 0.5);
    }

    public boolean cancel()
    {
        return cancel;
    }

    public void cleanup(){}
}
