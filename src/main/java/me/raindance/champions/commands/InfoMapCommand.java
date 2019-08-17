package me.raindance.champions.commands;

import me.raindance.champions.game.map.types.DominateMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InfoMapCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1) sender.sendMessage(new DominateMap(Bukkit.getWorld(args[0]), args[0]).getInfo());
        else sender.sendMessage("I require 1 argument of a map name");
        return true;
    }
}
