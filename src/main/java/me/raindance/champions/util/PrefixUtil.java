package me.raindance.champions.util;

import me.raindance.champions.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class PrefixUtil {
    public static String getPrefix(String role){
        StringBuilder prefix = new StringBuilder();
        //IMPORTANT: update this method when you add a new role

        switch(role) {
            case "developer":
                prefix.append(ChatColor.RED);
                prefix.append(ChatColor.BOLD);
                prefix.append("DEV ");
                break;
            case "host":
                prefix.append(ChatColor.GREEN);
                prefix.append(ChatColor.BOLD);
                prefix.append("HOST ");
                break;
            case "none": return "";
        }

        return prefix.toString();
    }

    public static String getPlayerRole(Player player) {
        FileConfiguration config = Main.getInstance().getConfig();
        for(String role: config.getConfigurationSection("roles").getKeys(false)){
            List<String> members = config.getStringList("roles." + role + ".players");
            if(members.contains(player.getUniqueId().toString())){
                return role;
            }
        }
        return "none";
    }
}
