package me.raindance.champions.commands;

import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvisCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("Champions.developer")) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    int duration = Integer.parseInt(args[0]);
                    StatusApplier.getOrNew(player).applyStatus(Status.STRENGTH, 50, duration);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sender.sendMessage(String.format("%sChampions> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}