package me.raindance.champions.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
import com.podcrash.api.mc.economy.Currency;
import com.podcrash.api.mc.events.econ.*;
import com.podcrash.api.mc.listeners.ListenerBase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyListener extends ListenerBase {
    public EconomyListener(JavaPlugin plugin) {
        super(plugin);
    }


    @EventHandler
    public void earn(PayEvent e) {
        //e.getPlayer().sendMessage("You earned " + e.getMoneys() + " !");
    }
    @EventHandler
    public void attempt(BuyAttemptEvent e) {
        e.getBuyer().sendMessage(String.format("%sEconomy> %sYou are buying %s%s %sfor %s%s %s%s.\n Use the command %s%s/confirm %s %sto complete your purchase.",
                ChatColor.BLUE, //Header
                ChatColor.GRAY, //Default color
                ChatColor.GREEN, //Item name
                e.getItem(), //Item
                ChatColor.GRAY, //Default color
                Currency.COIN.getFormatting(),
                e.getCost(), // cost
                ChatColor.GRAY, //default color
                Currency.COIN.getName(),
                ChatColor.YELLOW, // Confirmation text
                ChatColor.BOLD,
                e.getItem(),
                ChatColor.GRAY
        ));
    }
    @EventHandler
    public void buy(BuySuccessEvent e) {
        //e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());

    }

    @EventHandler
    public void fail(BuyFaliureEvent e) {
        //e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());
        //.getBuyer().sendMessage("but ur poor");
    }


    @EventHandler
    public void confirm(BuyConfirmEvent e) {
        e.getBuyer().sendMessage(String.format(
                "%SEconomy> %sYou have purchased %s%s%s for %s%s%s %s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                e.getItem(),
                ChatColor.GRAY,
                Currency.COIN.getFormatting(),
                e.getCost(),
                ChatColor.GRAY,
                Currency.COIN.getName()
                ));

        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        table.updateAllowedSkills(e.getBuyer().getUniqueId(), e.getItem());

    }
}
