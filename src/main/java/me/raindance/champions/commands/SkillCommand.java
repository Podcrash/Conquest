package me.raindance.champions.commands;

import com.podcrash.api.commands.CommandBase;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                ChampionsPlayer targetChampionsPlayer = (ChampionsPlayer) KitPlayerManager.getInstance().getKitPlayer(target);
                if(targetChampionsPlayer == null) {
                    player.sendMessage(ChatColor.BOLD + args[0] + "doesn't have any skills!");
                } else {
                    player.sendMessage(targetChampionsPlayer.skillsRead());
                }
            } else {
                ChampionsPlayer championsPlayer = (ChampionsPlayer) KitPlayerManager.getInstance().getKitPlayer((Player) sender);
                if(championsPlayer == null)
                    player.sendMessage(ChatColor.BOLD + "You currently don't have any skills!");
                else player.sendMessage(championsPlayer.skillsRead());
            }
        }
        return true;
    }
}
