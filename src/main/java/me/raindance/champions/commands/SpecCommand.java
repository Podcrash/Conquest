package me.raindance.champions.commands;

import me.raindance.champions.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpecCommand extends CommandBase{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            GameManager.addSpectator((Player) sender);
            return true;
        }
        return false;
    }
}
