package me.raindance.champions.commands;

import me.raindance.champions.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;

public class SetRoleCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("You must provide a player and a role!");
            return true;
        }
        String playerUUID = null;
        if(Bukkit.getPlayer(args[1]) != null) {
            playerUUID = Bukkit.getPlayer(args[1]).getUniqueId().toString();
        }else if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()){
            playerUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
        }
        if(playerUUID == null) {
            sender.sendMessage("Player " + args[1] + " has never joined this server before!");
            return true;
        }
        if(args.length == 2 && sender.isOp() && isValidRole(args[0])) {
            FileConfiguration config = Main.getInstance().getConfig();
            List<String> members = config.getStringList("roles." + args[0] + ".players");
            members.add(playerUUID);
            config.set("roles." + args[0] + ".players", members);
            Main.getInstance().saveConfig();
            sender.sendMessage("Successfully added " + args[1] + "'s role.");
            return true;
        } else if (args.length == 3 && sender.isOp() && args[2].equalsIgnoreCase("remove") && isValidRole(args[0])) {
            FileConfiguration config = Main.getInstance().getConfig();
            List<String> members = config.getStringList("roles." + args[0] + ".players");
            if(members.contains(playerUUID)) {
                members.remove(playerUUID);
                config.set("roles." + args[0] + ".players", members);
                Main.getInstance().saveConfig();
                sender.sendMessage("Successfully removed " + args[1] + "'s role.");
                return true;
            } else {
                sender.sendMessage("That player doesn't have the role you are trying to remove.");
                return true;
            }
        }
        return false;
    }

    private boolean isValidRole(String roleName) {
        for(String roles : Main.getInstance().getConfig().getConfigurationSection("roles").getKeys(false)) {
            if(roles.equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }
}
