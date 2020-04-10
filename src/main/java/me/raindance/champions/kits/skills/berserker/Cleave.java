package me.raindance.champions.kits.skills.berserker;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;


@SkillMetadata(id = 104, skillType = SkillType.Berserker, invType = InvType.DROP)
public class Cleave extends Drop implements ICooldown {
    private float multiplier;
    public Cleave() {
        super();
        this.multiplier = 1.0F;
    }

    @Override
    public float getCooldown() {
        return 12;
    }

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.zombie.metal", 2F, 90);
        return true;
        //TODO: Cleave particle effects?
    }

    @Override
    public String getName() {
        return "Cleave";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void damage(DamageApplyEvent e){
        if(e.isCancelled() || !isActive()) return;
        if(e.getAttacker() != getPlayer() || e.getCause() != Cause.MELEE) return;
        Location victLoc = e.getVictim().getLocation();
        for(Player player : getPlayers()){
            //TODO change it so that it will affect players based on vector
            if(isAlly(player) || e.getVictim() == player || getPlayer() == player || victLoc.distanceSquared(player.getLocation()) > 4D) continue;
            DamageApplier.damage(player, getPlayer(), e.getDamage() * (double) multiplier, this, true);
            DamageApplier.nativeApplyKnockback(player, getPlayer());
        }
    }

    private boolean isActive() {
        return System.currentTimeMillis() - getLastUsed() <= 4000L;
    }
}

