package me.raindance.champions.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.mc.events.econ.*;
import com.podcrash.api.mc.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyListener extends ListenerBase {
    public EconomyListener(JavaPlugin plugin) {
        super(plugin);
    }


    @EventHandler
    public void earn(PayEvent e) {
        e.getPlayer().sendMessage("You earned " + e.getMoneys() + "!");
    }
    @EventHandler
    public void attempt(BuyAttemptEvent e) {
        e.getBuyer().sendMessage("You are buying " + e.getItem() + " for " + e.getCost());
    }
    @EventHandler
    public void buy(BuySuccessEvent e) {
        e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());

    }

    @EventHandler
    public void fail(BuyFaliureEvent e) {
        e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());
        e.getBuyer().sendMessage("but ur poor");
    }


    @EventHandler
    public void confirm(BuyConfirmEvent e) {
        e.getBuyer().sendMessage("You bought " + e.getItem() + " for " + e.getCost());

        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        table.updateAllowedSkills(e.getBuyer().getUniqueId(), e.getItem());

    }
}
