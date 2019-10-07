package me.raindance.champions.kits.skills.KnightSkills;


import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;

//How to make a class from scratch
public class Vengeance extends Passive {//it is a passive because the other skilltypes do not match what I am trying to do: control damage com.podcrash.api.mc.events.
    //It's going to implement charges, since it has the ability to stack (at least twice). Nevermind, it is only going to implement the part where
    //it will gain charges, the other stuff has stuff to do with time.
    private final int MAX_LEVEL = 3;
    private float damageScale;
    private int maxDamage; //the damage you are allowed to give
    private int charges = 0;
    private long currentTime;

    public Vengeance(Player player, int level) {
        super(player, "Vengeance", level, SkillType.Knight, InvType.PASSIVEB);
        this.damageScale = 0.5f * level;
        this.maxDamage = level;
        setDesc(Arrays.asList(
                "When you attack someone, your damage ",
                "is increased by %%damage%% for each time the ",
                "enemy hit you since you last hit them, ",
                "up to a maximum of %%max%% bonus damage. "
        ));
        addDescArg("damage", () ->  damageScale);
        addDescArg("max", () -> maxDamage);
        //setting up the constructor.
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL; //giving it a max level
    }

    @EventHandler(
            priority = EventPriority.NORMAL
    )
    protected void hit(DamageApplyEvent e) {// looking into the hit com.podcrash.api.mc.events
        if (e.isCancelled()) return; //if the event is already cancelled, ie riposte, then don't do anything.
        /*
           There are two situations: one in which you are the victim and one in which you are the damager
         */
        if (currentTime - System.currentTimeMillis() >= 5000L) {
            resetCharges();
        }
        if (e.getCause() == Cause.MELEE) {
            if (e.getAttacker() == getPlayer()) {
                e.setModified(true); //Modify the damage
                e.addSkillCause(this);
                e.setDamage(e.getDamage() + getCurrentCharges() * damageScale); //set it
                resetCharges(); //reset the charges so that it's not infinite
            } else if (e.getVictim() == getPlayer()) {
                addCharge(); //if hit, add another charge
                currentTime = System.currentTimeMillis();
            }
        }

    }

    //all of these are set to private so that charges cannot be changed outside of themselves
    private void addCharge() {
        charges++;
        if (charges * damageScale > maxDamage) {
            charges = getMaxCharges();
        }
    }

    private int getCurrentCharges() { //get the current charges
        return charges;
    }

    private int getMaxCharges() {
        return (int) (maxDamage / damageScale);
        //level maxDamage damageScale
        //1 1 0.5
        //2 2 1
        //3 3 1.5
    }

    private void resetCharges() {
        charges = 0;
    }
}
