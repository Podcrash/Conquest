package me.raindance.champions.util;

import me.raindance.champions.Main;
import me.raindance.champions.Perm;
import me.raindance.champions.db.DataTableType;
import me.raindance.champions.db.PlayerPermissionsTable;
import me.raindance.champions.db.TableOrganizer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.nio.file.attribute.GroupPrincipal;
import java.util.List;
import java.util.Set;

public class PrefixUtil {
    public static String getPrefix(Perm role){
        StringBuilder prefix = new StringBuilder();
        //IMPORTANT: update this method when you add a new role
        if(role == null) return "";
        switch(role) {
            case DEVELOPER:
                prefix.append(ChatColor.RED);
                prefix.append(ChatColor.BOLD);
                prefix.append("DEV ");
                break;
            case HOST:
                prefix.append(ChatColor.GREEN);
                prefix.append(ChatColor.BOLD);
                prefix.append("HOST ");
                break;
            default: return "";
        }

        return prefix.toString();
    }

    public static Perm getPlayerRole(Player player) {
        PlayerPermissionsTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        List<Perm> perms = table.getRoles(player.getUniqueId());
        for(Perm perm : Perm.values()) {
            if(perms.contains(perm)) {
                return perm;
            }
        }
        return null;
    }
}
