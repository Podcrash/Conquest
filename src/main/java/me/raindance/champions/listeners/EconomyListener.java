package me.raindance.champions.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.mc.events.econ.BuyConfirmEvent;
import com.podcrash.api.mc.events.econ.BuySuccessEvent;
import com.podcrash.api.mc.listeners.ListenerBase;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyListener extends ListenerBase {
    public EconomyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void buy(BuySuccessEvent e) {
        e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());

    }

    @EventHandler
    public void confirm(BuyConfirmEvent e) {
        e.getBuyer().sendMessage("You bought " + e.getItem() + " for " + e.getCost());

        PlayerTable table = TableOrganizer.getTable(DataTableType.PLAYERS);

    }
}
