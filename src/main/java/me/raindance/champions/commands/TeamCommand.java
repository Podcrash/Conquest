package me.raindance.champions.commands;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && !GameManager.isSpectating(player)) {
                if (args[0].equalsIgnoreCase("red") || args[0].equalsIgnoreCase("blue")) {
                    String team = args[0].toLowerCase();
                    if (GameManager.hasPlayer(player) && !GameManager.getGame().isOngoing()) {
                        GameManager.joinTeam(player, TeamEnum.getByColor(team));
                    } else if(GameManager.getGame().isOngoing()) {
                        player.sendMessage(
                                String.format(
                                        "%sConquest> %sYou may not switch teams mid-game!",
                                        ChatColor.BLUE,
                                        ChatColor.GRAY));
                    }else player.sendMessage(
                            String.format(
                                    "%sConquest> %sYou are not currently in a game.",
                                    ChatColor.BLUE,
                                    ChatColor.GRAY));

                } else player.sendMessage(
                        String.format(
                                "%sConquest> %sValid arguments are 'red' and 'blue'.",
                                ChatColor.BLUE,
                                ChatColor.GRAY));

                return true;
            } else if(GameManager.isSpectating(player)) player.sendMessage(String.format(
                    "%sConquest> %sYou are currently spectating this game.",
                    ChatColor.BLUE,
                    ChatColor.GRAY));
            else player.sendMessage(String.format(
                        "%sConquest> %sValid arguments are 'red' and 'blue'.",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
        }
        return true;
    }
}
