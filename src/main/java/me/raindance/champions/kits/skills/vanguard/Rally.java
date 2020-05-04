package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import me.raindance.champions.annotation.kits.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Drop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

@SkillMetadata(id = 806, skillType = SkillType.Vanguard, invType = InvType.DROP)
public class Rally extends Drop implements ICooldown {
    private final double radiusSquared = 5 * 5;
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

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        Location currentLoc = getPlayer().getLocation();
        SoundPlayer.sendSound(currentLoc, "mob.horse.gallop", 0.75F, 1);
        for(Player other : getPlayers()) {
            if(other != getPlayer() //In lobby, isAlly may not work properly
                    && !isAlly(other)) continue;
            Location otherLoc = other.getLocation();
            double distSquared = currentLoc.distanceSquared(otherLoc);
            if(distSquared > radiusSquared) continue;
            StatusApplier.getOrNew(other).applyStatus(Status.SPEED, 5, 1, true, true);
        }
        return true;
    }
}
