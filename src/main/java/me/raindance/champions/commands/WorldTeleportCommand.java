package me.raindance.champions.commands;

import me.raindance.champions.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTeleportCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("Champions.developer")) {
            if (args.length > 0) {
                String worldName = args[0];
                WorldManager.getInstance().teleport((Player) sender, worldName);
            } else sender.sendMessage("Specify a world to teleport to");
            return true;
        } else {
            sender.sendMessage(String.format("%sChampions> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
