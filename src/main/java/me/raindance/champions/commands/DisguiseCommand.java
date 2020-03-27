package me.raindance.champions.commands;

import com.podcrash.api.mc.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisguiseCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.developer")) {
            Player player = (Player) sender;
            if (args.length == 1) {
                UUID uuid = Bukkit.getPlayer(args[0]).getUniqueId();
                Player disguisedas = (Player) Bukkit.getOfflinePlayer(uuid);
                sender.sendMessage(disguisedas.getName() + " " + uuid.toString());
            }
        } else {
            sender.sendMessage(String.format("%sConquest> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
