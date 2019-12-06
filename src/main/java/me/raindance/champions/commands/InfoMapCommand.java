package me.raindance.champions.commands;

import com.podcrash.api.mc.map.MapManager;
import me.raindance.champions.game.map.DominateMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InfoMapCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            MapManager.getMap(DominateMap.class, args[0], dominateMap -> {
                sender.sendMessage(dominateMap.getInfo());
            });
        }else sender.sendMessage("I require 1 argument of a map name");
        return true;
    }
}
