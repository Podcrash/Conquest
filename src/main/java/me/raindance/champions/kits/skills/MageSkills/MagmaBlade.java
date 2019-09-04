package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class MagmaBlade extends Passive {
    private final int MAX_LEVEL = 3;
    private int extraDamage;

    public MagmaBlade(Player player, int level) {
        super(player, "Magma Blade", level, SkillType.Mage, InvType.PASSIVEB);
        extraDamage = level;
        setDesc(Arrays.asList(
                "Your sword deals an additional ",
                "%%damage%% damage to burning opponents, ",
                "but it also extinguishes them. "
        ));
        addDescArg("damage", () ->  extraDamage);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @EventHandler
    public void hit(DamageApplyEvent event){
        if(event.isCancelled()) return;
        if(event.getCause() != Cause.MELEE) return;
        if (event.getAttacker() == getPlayer() && event.getVictim().getFireTicks() > 0 && getItemType(getPlayer().getItemInHand()) == ItemType.SWORD) {
            event.setDamage(event.getDamage() + extraDamage);
            event.setModified(true);
            event.addSkillCause(this);
            event.getVictim().setFireTicks(0);
        } else {
            System.out.println(event.getVictim().getFireTicks());
        }
    }
}
