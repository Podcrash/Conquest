package me.raindance.champions.events;

import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ApplyKitEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private ChampionsPlayer championsPlayer;
    private boolean keepInventory;
    private boolean cancel;

    public ApplyKitEvent(ChampionsPlayer championsPlayer) {
        this.championsPlayer = championsPlayer;
        this.keepInventory = false;
    }

    public ChampionsPlayer getChampionsPlayer() {
        return championsPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
