package me.raindance.champions.commands;

import me.raindance.champions.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ViewCommand extends CommandBase{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            int id = Integer.parseInt(args[0]);
            if(GameManager.getGame(id) != null) {
                sender.sendMessage(GameManager.getGame(id).toString());
            }else sender.sendMessage("Game with ID " + id + " does not exist!");
            return true;
        }
        return false;
    }
}
