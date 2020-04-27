package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.time.resources.TimeResource;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.IPassiveTimer;
import com.podcrash.api.kits.skilltypes.Passive;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SkillMetadata(id = 309, skillType = SkillType.Duelist, invType = InvType.INNATE)
public class Challenger extends Passive implements IPassiveTimer, TimeResource {
    @Override
    public void start() {
        runAsync(5,0);
    }

    @Override
    public void task() {
        if(hasStarBuff()) return;
        int i = 0;
        Location location = getPlayer().getLocation();
        for(Player player : getGame().getBukkitPlayers()) {
            if(player == getPlayer() || getGame().isRespawning(player) || getGame().isSpectating(player)) continue;
            if(location.distanceSquared(player.getLocation()) > 25) continue;
            i++;
            if(i > 1) {
                removeBuff();
                return;
            }
        }
        if(i == 0) {
            removeBuff();
            return;
        }
        applyBuff();
    }

    /**
     * I kinda hate this solution :/
     * @return
     */
    private boolean hasStarBuff() {
        if(GameManager.getGame() == null) return false;
        DomGame game = (DomGame) GameManager.getGame();
        return game.getStarBuff().getCollector() == this.getPlayer();
    }
    private void removeBuff() {
        StatusApplier ap = StatusApplier.getOrNew(getPlayer());
        if(ap.has(Status.RESISTANCE) && ap.has(Status.STRENGTH)) {
            ap.removeStatus(Status.STRENGTH, Status.RESISTANCE);
        }
    }
    private void applyBuff() {

        StatusApplier ap = StatusApplier.getOrNew(getPlayer());
        ap.applyStatus(Status.STRENGTH, Integer.MAX_VALUE, 0);
        ap.applyStatus(Status.RESISTANCE, Integer.MAX_VALUE, 0);
    }
    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public String getName() {
        return "Challenger";
    }
}
