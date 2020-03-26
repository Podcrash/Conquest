package me.raindance.champions.commands;

import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        EconomyHandler handler = (EconomyHandler) Pluginizer.getSpigotPlugin().getEconomyHandler();
        p.sendMessage(String.format("%sEconomy> %sYour crystals: %s%s%s", ChatColor.BLUE, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, ChatColor.BOLD, handler.getMoney(p)));
        return true;
    }
}
