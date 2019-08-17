package me.raindance.champions.commands;

import me.raindance.champions.Main;
import me.raindance.champions.kits.ChampionsPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;

public class SetRoleCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 2 && sender.isOp() && isValidRole(args[0])) {
            FileConfiguration config = Main.getInstance().getConfig();
            List<String> members = config.getStringList("roles." + args[0] + ".players");
            members.add(args[1]);
            config.set("roles." + args[0] + ".players", members);
            Main.getInstance().saveConfig();
            sender.sendMessage("Successfully added " + args[1] + "'s role.");
            return true;
        } else if (args.length == 3 && sender.isOp() && args[2].equalsIgnoreCase("remove") && isValidRole(args[0])) {
            FileConfiguration config = Main.getInstance().getConfig();
            List<String> members = config.getStringList("roles." + args[0] + ".players");
            if(members.contains(args[1])) {
                members.remove(args[1]);
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
            if(roles.equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
