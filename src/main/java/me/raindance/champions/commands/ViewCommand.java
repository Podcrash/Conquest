package me.raindance.champions.commands;

import com.podcrash.api.mc.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ViewCommand extends CommandBase{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(GameManager.getGame() != null) {
            sender.sendMessage(GameManager.getGame().toString());
        }else sender.sendMessage(String.format(
                "%sConquest> %sA game has not been created yet.",
                ChatColor.BLUE,
                ChatColor.GRAY));
        return true;
    }
}
