package me.raindance.champions.commands;

import com.podcrash.api.commands.CommandBase;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import me.raindance.champions.kits.ChampionsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            ChampionsPlayer p = (ChampionsPlayer) KitPlayerManager.getInstance().getChampionsPlayer((Player) sender);
            if(p == null)
                player.sendMessage(ChatColor.BOLD + "You currently don't have any skills!");
            else p.skillsRead();
        }
        return true;
    }
}
