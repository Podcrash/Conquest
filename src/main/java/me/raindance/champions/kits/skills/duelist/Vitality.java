package me.raindance.champions.kits.skills.duelist;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.events.StatusRemoveEvent;
import com.podcrash.api.mc.events.game.GameResurrectEvent;
import com.podcrash.api.mc.events.game.GameStartEvent;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IPassiveTimer;
import me.raindance.champions.kits.skilltypes.Passive;
import org.bukkit.Statistic;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

@SkillMetadata(id = 311, skillType = SkillType.Duelist, invType = InvType.SECONDARY_PASSIVE)
public class Vitality extends Passive implements IPassiveTimer{
    private long timeOfLastCancel;
    private boolean tierTwoEnabled = false;
    private final int tierTwoTime = 60; //SECONDS till tier 2 health boost

    @Override
    public String getName() {
        return "Vitality";
    }

    @Override
    public void init() {
        start();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(GameResurrectEvent event) {
        if (event.getWho() != getPlayer()) return;
        start();
    }

    @Override
    public void start() {
        timeOfLastCancel = System.currentTimeMillis();
        TimeHandler.delayTime(2, () -> {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.HEALTH_BOOST, 10000, 0);
            getPlayer().setHealth(getPlayer().getMaxHealth());
        });
        TimeHandler.delayTime(20 * tierTwoTime, () -> {
            if (System.currentTimeMillis() - timeOfLastCancel < 20 * tierTwoTime) return;
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.HEALTH_BOOST, 10000, 1, true, true);
            getPlayer().setHealth(getPlayer().getMaxHealth());
        });
    }

    @Override
    public void stop() {
    }

    @EventHandler
    public void gameStart(GameStartEvent event) {
        start();
    }

    @EventHandler
    public void onDeath(DeathApplyEvent event) {
        if (event.getPlayer() == getPlayer()) timeOfLastCancel = System.currentTimeMillis();
    }

}
