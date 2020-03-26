package me.raindance.champions.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.PlayerTable;
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
        e.getBuyer().sendMessage(String.format("%sEconomy> %sYou are buying %s%s %sfor %s%s%s %scrystals.\n Use the command %s%s/confirm %s %sto complete your purchase.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                e.getItem(),
                ChatColor.GRAY,
                ChatColor.LIGHT_PURPLE,
                ChatColor.BOLD,
                e.getCost(),
                ChatColor.GRAY,
                ChatColor.YELLOW,
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
                "%SEconomy> %sYou have purchased %s%s%s for %s%s%s%s crystals.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                e.getItem(),
                ChatColor.GRAY,
                ChatColor.LIGHT_PURPLE,
                ChatColor.BOLD,
                e.getCost(),
                ChatColor.GRAY
                ));

        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        table.updateAllowedSkills(e.getBuyer().getUniqueId(), e.getItem());

    }
}
