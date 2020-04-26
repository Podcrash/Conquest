package me.raindance.champions.commands;

import com.podcrash.api.commands.CommandBase;
import me.raindance.champions.listeners.InventoryListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LockCommand extends CommandBase {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("invicta.lock")) return true;
        InventoryListener.lock = !InventoryListener.lock;

        sender.sendMessage("lock: " + InventoryListener.lock);
        return true;
    }
}
