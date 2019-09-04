package me.raindance.champions.commands;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (GameManager.hasPlayer(player)) {
                Game game = GameManager.getGame();
                if(game == null) return false; //this should never happen, but whatever
                if(game.isOngoing()) {
                    player.sendMessage(
                            String.format(
                                    "%sChampions> %sYou cannot leave mid-game!",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY));
                }else {
                    GameManager.removePlayer(player);
                    player.sendMessage(
                            String.format(
                                    "%sChampions> %sYou have left %sGame %s%s.",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY,
                                    ChatColor.GREEN,
                                    game.getId(),
                                    ChatColor.GRAY));
                }
            } else {
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou are not currently in a game.",
                                ChatColor.BLUE,
                                ChatColor.GRAY));
            }
            return true;
        }
        return false;
    }
}
