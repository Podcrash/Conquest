package me.raindance.champions.commands;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import me.raindance.champions.game.GameType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (GameManager.hasPlayer(player)) {
                player.sendMessage(
                        String.format(
                                "%sChampions> %sYou are already in a game.",
                                ChatColor.BLUE,
                                ChatColor.GRAY));
            } else if (!GameManager.anyGames()) {
                Game game = GameManager.createGame(Long.toString(System.currentTimeMillis()), GameType.DOM);
                GameManager.addPlayer(game.getId(), player);
            } else {
                if (args.length == 1) {
                    GameManager.addPlayer(Integer.parseInt(args[0]), player);
                } else {
                    StringBuilder builder = new StringBuilder();
                    List<Game> games = GameManager.getGames();
                    for(Game game : games) {
                        builder.append("(ID: ");
                        builder.append(game.getId());
                        builder.append(' ');
                        builder.append(game.getPlayerCount());
                        builder.append('/');
                        builder.append(game.getMaxPlayers());
                        builder.append(')');
                        builder.append("\n");
                    }
                    player.sendMessage("Available Games to Join: \n" + builder.toString());
                    player.sendMessage("/join <id> to join the game!");
                    player.sendMessage(
                            String.format(
                                    "%sChampions> %sUse the command %s/join <id> %sto join a game!",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY,
                                    ChatColor.GREEN,
                                    ChatColor.GRAY));
                }
            }
            return false;
        }
        return true;
    }
}
/*
for(Game me.raindance.champions.game : GameManager.getGames()){
                    if(!me.raindance.champions.game.isOngoing()){
                        GameManager.addPlayer(me.raindance.champions.game.getId(), player);
                    }
                }
 */