package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Drop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

@SkillMetadata(id = 806, skillType = SkillType.Vanguard, invType = InvType.DROP)
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

    @Override
    public boolean drop(PlayerDropItemEvent e) {
        if(onCooldown()) return false;
        setLastUsed(System.currentTimeMillis());
        Location currentLoc = getPlayer().getLocation();
        SoundPlayer.sendSound(currentLoc, "mob.horse.gallop", 0.75F, 1);
        for(Player other : getPlayers()) {
            if(!isAlly(other)) continue;
            Location otherLoc = other.getLocation();
            double distSquared = currentLoc.distanceSquared(otherLoc);
            if(distSquared > 16D) continue;
            StatusApplier.getOrNew(other).applyStatus(Status.SPEED, 5, 1, true, true);
        }
        return true;
    }
}
