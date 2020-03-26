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

            if(GameManager.getGame().isSpectating((Player) sender)) GameManager.getGame().toggleSpec((Player) sender);

            Player player = (Player) sender;
            // Check to make sure the sender has included the proper arguments to use this command.
            if (args.length == 1) {
                // Check to make sure the sender has either chosen the arguments "red" or "blue"
                if (args[0].equalsIgnoreCase("red") || args[0].equalsIgnoreCase("blue")) {
                    // This string "team" must either be "red" or "blue": this is so TeamEnum.getByColor() won't get confused.
                    String team = args[0].toLowerCase();
                    if (!GameManager.getGame().isOngoing()) {
                        GameManager.joinTeam(player, TeamEnum.getByColor(team));
                    } else {
                        player.sendMessage(
                                String.format(
                                        "%sConquest> %sYou may not switch teams mid-game!",
                                        ChatColor.BLUE,
                                        ChatColor.GRAY));
                    }

                } else player.sendMessage(
                        String.format(
                                "%sConquest> %sValid arguments are 'red' and 'blue'.",
                                ChatColor.BLUE,
                                ChatColor.GRAY));

                return true;
            }
        }
        return false;
    }
}
