package me.raindance.champions.commands;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("Champions.host") && args.length == 0) {
            Player player = (Player) sender;
            if (GameManager.hasPlayer(player)) {
                Game game = GameManager.getGame(player);
                if(game == null || game.isOngoing()) {
                    sender.sendMessage("Game has started already!");
                    return false;
                }
                log(game.toString());
                if (game.isLoadedMap()) {
                    GameManager.startGame(game);
                } else player.sendMessage("A map has not been set for Game #" + game.getId());
            } else player.sendMessage("You are currently not in a game");
        } else if(sender.hasPermission("Champions.host") && args.length == 1){
            Game game = GameManager.getGame(Integer.parseInt(args[0]));
            if(game == null || game.isOngoing()) {
                sender.sendMessage("Game has started already!");
                return false;
            }
            log(game.toString());
            if (game.isLoadedMap()) {
                GameManager.startGame(game);
            } else log("A map has not been set for Game #" + game.getId());
        } else {
            sender.sendMessage(String.format("%sChampions> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
