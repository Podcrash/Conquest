package me.raindance.champions.commands;

import com.podcrash.api.mc.damage.DamageQueue;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                ChampionsPlayer cplayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
                if(cplayer == null) return true;
                if(!cplayer.isInGame()){
                    sender.sendMessage(String.format("%sConquest> %sYou must be in a game to use this command!", ChatColor.BLUE, ChatColor.GRAY));
                    return true;
                }
                if(cplayer.getGame().isOngoing() && !cplayer.getGame().isSpectating(player)){
                    if(cplayer.getGame().isRespawning(player)) {
                        sender.sendMessage(String.format("%sConquest> %sYou are already dead.", ChatColor.BLUE, ChatColor.GRAY));
                        return true;
                    }
                    DamageQueue.artificialDie(player);
                    return true;
                }
            }
        }
        return false;
    }
}
