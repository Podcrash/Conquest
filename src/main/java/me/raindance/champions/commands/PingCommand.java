package me.raindance.champions.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.util.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                player.sendMessage(String.format("Your ping: %d", Utility.ping(player)));
            }else if(args.length == 1){
                Player p = Bukkit.getPlayer(args[0]);
                if(p == null){
                    player.sendMessage(String.format("Is %s a player?", args[0]));
                    return false;
                }else{

                    player.sendMessage(String.format("%s's ping: %d", p.getName(), Utility.ping(p)));
                }
            }
            return true;
        }
        return false;
    }
}
