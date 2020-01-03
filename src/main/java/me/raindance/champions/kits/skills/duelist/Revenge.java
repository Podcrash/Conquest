package me.raindance.champions.kits.skills.duelist;


import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//How to make a class from scratch
@SkillMetadata(skillType = SkillType.Duelist, invType = InvType.PASSIVEA)
public class Revenge extends Passive implements ICooldown {//it is a passive because the other skilltypes do not match what I am trying to do: control damage
    //It's going to implement charges, since it has the ability to stack (at least twice). Nevermind, it is only going to implement the part where
    //it will gain charges, the other stuff has stuff to do with time.
    private long currentTime;
    private boolean dealBonus;
    @Override
    public String getName() {
        return "Revenge";
    }

    @Override
    public float getCooldown() {
        return 5;
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

        //we only care about melee attacks
        if (e.getCause() != Cause.MELEE) return;

        if (e.getAttacker() == getPlayer() && dealBonus) {
            e.setModified(true); //Modify the damage
            e.addSource(this);
            final double bonus = 2;
            e.setDamage(e.getDamage() + bonus); //set it
            reset(); //reset the charges so that it's not infinite
        } else if (e.getVictim() == getPlayer()) {
            dealBonus = true;
            currentTime = System.currentTimeMillis();
        }

    }

    public void reset() {
        dealBonus = false;
    }

}
