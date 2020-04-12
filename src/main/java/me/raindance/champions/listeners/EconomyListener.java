package me.raindance.champions.listeners;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.ChampionsKitTable;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.mc.economy.Currency;
import com.podcrash.api.mc.events.econ.*;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.listeners.ListenerBase;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.inventory.InvFactory;
import me.raindance.champions.inventory.MenuCreator;
import me.raindance.champions.inventory.SkillData;
import me.raindance.champions.kits.SkillInfo;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class EconomyListener extends ListenerBase {
    private HashMap<Player, Inventory> savedInventories;

    public EconomyListener(JavaPlugin plugin) {
        super(plugin);
        savedInventories = new HashMap<>();
    }


    @EventHandler
    public void earn(PayEvent e) {
        Game game = GameManager.getGame();
        double money = e.getMoneys();
        if(money < 0) return;
        if(game != null && game.getGameState() == GameState.STARTED) {
            game.addReward(e.getPlayer(), e.getMoneys());
        }
        e.getPlayer().sendMessage(Currency.GOLD.getColor() + "+" + e.getMoneys() + " " + Currency.GOLD.getName() + "!");
        SoundPlayer.sendSound(e.getPlayer(), "random.orb", 0.25F, 63);
    }
    @EventHandler
    public void attempt(BuyAttemptEvent e) {
        /*
        e.getBuyer().sendMessage(String.format("%sEconomy> %sYou are buying %s%s %sfor %s%s %s%s.\n Use the command %s%s/confirm %s %sto complete your purchase.",
                ChatColor.BLUE, //Header
                ChatColor.GRAY, //Default color
                ChatColor.GREEN, //Item name
                e.getItem(), //Item
                ChatColor.GRAY, //Default color
                Currency.GOLD.getFormatting(),
                e.getCost(), // cost
                ChatColor.GRAY, //default color
                Currency.GOLD.getName(),
                ChatColor.YELLOW, // Confirmation text
                ChatColor.BOLD,
                e.getItem(),
                ChatColor.GRAY
        ));
         */

    }
    @EventHandler
    public void buy(BuySuccessEvent e) {
        //e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());
        Inventory inv = MenuCreator.createConfirmationMenu(e.getItem(),e.getCost());
        e.getBuyer().closeInventory();
        e.getBuyer().openInventory(inv);

    }

    @EventHandler
    public void fail(BuyFailureEvent e) {
        //e.getBuyer().sendMessage("You attempted to buy " + e.getItem() + " for " + e.getCost());
        //.getBuyer().sendMessage("but ur poor");
        SkillData data = SkillInfo.getSkillFromStrippedName(e.getItem());
        e.getBuyer().sendMessage(String.format(
                "%SEconomy> %sYou need %s%s %sto purchase %s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                Currency.GOLD.getFormatting(),
                e.getCost(),
                ChatColor.GRAY,
                data.getName()
        ));
    }


    @EventHandler
    public void confirm(BuyConfirmEvent e) {
        SkillData data = SkillInfo.getSkillFromStrippedName(e.getItem());
        e.getBuyer().sendMessage(String.format(
                "%SEconomy> %sYou have purchased %s%s%s for %s%s%s %s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                data.getName(),
                ChatColor.GRAY,
                Currency.GOLD.getFormatting(),
                e.getCost(),
                ChatColor.GRAY,
                Currency.GOLD.getName()
                ));


        ChampionsKitTable table = TableOrganizer.getTable(DataTableType.KITS);
        table.updateAllowedSkills(e.getBuyer().getUniqueId(), e.getItem());

        InvFactory.edit(e.getBuyer(),
                InvFactory.getLastestSkillType(e.getBuyer()),
                InvFactory.getLatestBuildID(e.getBuyer()));

    }

    @EventHandler
    public void cancel(BuyCancelEvent e) {
        e.getBuyer().sendMessage(String.format(
                "%SEconomy> %sYou have cancelled the purchase.",
                ChatColor.BLUE,
                ChatColor.GRAY
        ));
        InvFactory.edit(e.getBuyer(),
                InvFactory.getLastestSkillType(e.getBuyer()),
                InvFactory.getLatestBuildID(e.getBuyer()));
    }
}
