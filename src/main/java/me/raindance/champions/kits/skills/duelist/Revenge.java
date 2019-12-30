package me.raindance.champions.kits.skills.duelist;


import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//How to make a class from scratch
@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.PASSIVEB)
public class Revenge extends Passive {//it is a passive because the other skilltypes do not match what I am trying to do: control damage
    //It's going to implement charges, since it has the ability to stack (at least twice). Nevermind, it is only going to implement the part where
    //it will gain charges, the other stuff has stuff to do with time.
    private long currentTime;
    private int charges = 0;
    @Override
    public String getName() {
        return "Revenge";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    protected void hit(DamageApplyEvent e) {// looking into the hit events
        if (e.isCancelled()) return; //if the event is already cancelled, ie riposte, then don't do anything.
        /*
           There are two situations: one in which you are the victim and one in which you are the damager
         */

        //if too much time (5 seconds) has passed, reset the charges
        if (currentTime - System.currentTimeMillis() >= 5000L)
            resetCharges();
        //we only care about melee attacks
        if (e.getCause() != Cause.MELEE) return;

        if (e.getAttacker() == getPlayer()) {
            e.setModified(true); //Modify the damage
            e.addSource(this);
            double bonus = getCurrentCharges() * 0.5D;
            e.setDamage(e.getDamage() + bonus); //set it
            resetCharges(); //reset the charges so that it's not infinite
        } else if (e.getVictim() == getPlayer()) {
            addCharge(); //if hit, add another charge
            currentTime = System.currentTimeMillis();
        }

    }

    //all of these are set to private so that charges cannot be changed outside of themselves
    private void addCharge() {
        charges++;
        if (charges > getMaxCharges()) {
            charges = getMaxCharges();
        }
    }

    private int getCurrentCharges() { //get the current charges
        return charges;
    }

    private int getMaxCharges() {
        return (int) (1 / 0.5);
        //level maxDamage damageScale
        //1 1 0.5
        //2 2 1
        //3 3 1.5
    }

    private void resetCharges() {
        charges = 0;
    }
}
