package me.raindance.champions.kits.skills.BruteSkills;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.classes.Brute;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IDropPassive;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

public class Rally extends Passive implements IDropPassive {
    public Rally(Player player, int level) {
        super(player, "Rally", 1, SkillType.Brute, InvType.AXE, 12);

        setDesc("Drop shovel/axe to activate. For 3 seconds, you and your",
                "allies within 3 blocks of you gain Speed II.");
    }

    @EventHandler
    @Override
    public void drop(PlayerDropItemEvent e) {
        if(onCooldown()) {
            getPlayer().sendMessage(getCooldownMessage());
            return;
        }
        if(checkItem(e.getItemDrop().getItemStack()) && e.getPlayer() == getPlayer()) {
            doSkill();
            setLastUsed(System.currentTimeMillis());
            e.setCancelled(true);
        }
    }

    @Override
    public void doSkill() {
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

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
