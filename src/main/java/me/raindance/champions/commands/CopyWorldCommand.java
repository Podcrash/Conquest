package me.raindance.champions.commands;

import com.podcrash.api.mc.commands.CommandBase;
import com.podcrash.api.mc.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CopyWorldCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player && sender.hasPermission("invicta.developer")){
            Player player = (Player) sender;
            if(args.length == 1){
                WorldManager worldManager = WorldManager.getInstance();
                World world = worldManager.copyWorld(args[0]);
                if(world != null){
                    worldManager.teleport(player, world.getName());
                    player.sendMessage(String.format("World %s was successfully created", world.getName()));
                }else player.sendMessage(String.format("Problem with creating world %s", args[0]));
            }else player.sendMessage("provide a name for the world");
        } else {
            sender.sendMessage(String.format("%sConquest> %sYou have insufficient permissions to use that command.", ChatColor.BLUE, ChatColor.GRAY));
        }
        return true;
    }
}
