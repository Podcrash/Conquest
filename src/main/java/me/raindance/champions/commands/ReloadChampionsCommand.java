package me.raindance.champions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadChampionsCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("invicta.developer")){
            Bukkit.getServer().dispatchCommand(sender, "plugman reload Champions");
        }else if(sender instanceof ConsoleCommandSender) {
            Bukkit.getServer().dispatchCommand(sender, "plugman reload Champions");
            return true;
        } else {
            sender.sendMessage(String.format("%sConquest> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
