package me.raindance.champions.commands;

import com.podcrash.api.permissions.Perm;
import me.raindance.champions.Main;
import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.PlayerPermissionsTable;
import com.podcrash.api.db.TableOrganizer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetRoleCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("You must provide a player and a role!");
            return true;
        }
        OfflinePlayer arg2Player = getUUID(args[1]);
        UUID playerUUID = arg2Player.getUniqueId();
        if(playerUUID == null) {
            sender.sendMessage("Player " + args[1] + " has never joined this server before!");
            return true;
        }
        Perm newRole = Perm.getBy(args[0]);
        if(newRole == null) {
            sender.sendMessage("Role " + args[0] + " is not a valid role!");
            return true;
        }
        if(!sender.isOp() && !Perm.DEVELOPER.has(sender)) {
            sender.sendMessage("You do not have permissions to run this command!");
            return true;
        }

        PlayerPermissionsTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);
        if(args.length == 2) {
            if(table.hasRole(playerUUID, newRole)) {
                table.removeRole(playerUUID, newRole);

                sender.sendMessage("Successful removed " + args[1] + "'s role:" + newRole.name());
            }else{

                table.addRole(playerUUID, newRole);
                sender.sendMessage("Successfully added " + args[1] + "'s role:" + newRole.name());
            }
            Player p;
            if((p = Bukkit.getPlayer(playerUUID)) != null) {
                Main.getInstance().setupPermissions(p);
            }
            return true;
        }
        return false;
    }

    /**
     * we have to use the uuid api to search via username
     * @param args
     * @return
     */
    public OfflinePlayer getUUID(String args) {
        if(Bukkit.getPlayer(args) != null) {
            return Bukkit.getPlayer(args);
        }else if (Bukkit.getOfflinePlayer(args).hasPlayedBefore()){
            return Bukkit.getOfflinePlayer(args);
        }
        return null;
    }
}
