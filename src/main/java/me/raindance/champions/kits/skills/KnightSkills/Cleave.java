package me.raindance.champions.kits.skills.KnightSkills;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class Cleave extends Passive {
    private float multiplier;
    public Cleave(Player player, int level) {
        super(player, "Cleave", level,  SkillType.Knight, InvType.PASSIVEA);
        this.multiplier = 0.25F + 0.25F * (float) level;
        setDesc(Arrays.asList(
                "You attacks deal %%rate%%% damage to ",
                "all enemies within 3 blocks ",
                "of your target enemy.",
                "",
                "This only works with Axes."
        ));
        addDescArg("rate", () ->  multiplier * 100);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @EventHandler
    public void damage(DamageApplyEvent e){
        if(e.getAttacker() != getPlayer() || e.getCause() != Cause.MELEE) return;
        Location victLoc = e.getVictim().getLocation();
        for(Player player : getPlayers()){
            //TODO change it so that it will affect players based on vector
            if(isAlly(player) || e.getVictim() == player || getPlayer() == player || victLoc.distanceSquared(player.getLocation()) > 9D) continue;
            DamageApplier.damage(player, getPlayer(), e.getDamage() * (double) multiplier, this, true);
            DamageApplier.nativeApplyKnockback(player, getPlayer());
        }
    }
}

