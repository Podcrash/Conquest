package me.raindance.champions.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.damage.DamageApplier;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DamageCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("invicta.developer")) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    int damage = Integer.parseInt(args[0]);
                    DamageApplier.damage(player, player, damage, false);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sender.sendMessage(String.format("%sConquest> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
    }
        return true;
    }
}
