package me.raindance.champions.commands;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("Champions.host")){
            if (!(sender instanceof Player)) return false;
            Player player = (Player) sender;
            if (!(args.length == 1)) return false;
            int id = Integer.parseInt(args[0]);
            Game game = GameManager.getGame(id);
            if(game == null) {
                sender.sendMessage("That is not a valid game!");
                return false;
            }
            GameManager.endGame(GameManager.getGame(id));
            player.sendMessage("attempting to end game " + id);
            return true;
        } else {
            sender.sendMessage(String.format("%sChampions> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}