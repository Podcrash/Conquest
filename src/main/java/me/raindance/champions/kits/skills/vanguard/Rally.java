package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IDropPassive;
import me.raindance.champions.kits.skilltypes.Drop;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

@SkillMetadata(skillType = SkillType.Vanguard, invType = InvType.DROP)
public class Rally extends Drop implements ICooldown {
    @Override
    public float getCooldown() {
        return 12;
    }

    @Override
    public String getName() {
        return "Rally";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }


    @EventHandler
    @Override
    public void drop(PlayerDropItemEvent e) {
        if(!onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        Location currentLoc = getPlayer().getLocation();
        SoundPlayer.sendSound(currentLoc, "mob.pig.death", 0.75F, 1);
        for(Player other : getPlayers()) {
            if(!isAlly(other)) continue;
            Location otherLoc = other.getLocation();
            double distSquared = currentLoc.distanceSquared(otherLoc);
            if(distSquared > 9D) continue;
            StatusApplier.getOrNew(other).applyStatus(Status.SPEED, 3, 1, true, true);
        }
    }
}