package me.raindance.champions.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

public class VelocityCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.developer")) {
            Player player = (Player) sender;
            if(args.length == 3) {
                player.setVelocity(player.getVelocity().add(new Vector(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]))));
            }
            /*
            Inventory inv = Bukkit.createInventory(null, Integer.parseInt(args[0]), "hello");
            player.openInventory(inv);
            */
        } else {
            sender.sendMessage(String.format("%sConquest> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
