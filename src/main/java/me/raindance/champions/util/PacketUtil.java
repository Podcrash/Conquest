package me.raindance.champions.util;

import com.comphenix.packetwrapper.AbstractPacket;
import me.raindance.champions.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class PacketUtil {
    private static final JavaPlugin plugin = Main.instance;

    public static void syncSend(AbstractPacket packet, List<Player> players) {
        Bukkit.getScheduler().runTask(plugin, () -> players.forEach(packet::sendPacket));
    }
    public static void syncSend(AbstractPacket packet, Player... players) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(Player player : players) packet.sendPacket(player);
        });
    }
    public static void syncSend(List<AbstractPacket> packets, List<Player> players) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void syncSend(AbstractPacket[] packets, List<Player> players) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void syncSend(AbstractPacket[] packets, Player... players) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void syncSend(List<AbstractPacket> packets, Player... players) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }

    public static void asyncSend(AbstractPacket packet, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> players.forEach(packet::sendPacket));
    }
    public static void asyncSend(AbstractPacket packet, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(Player player : players) packet.sendPacket(player);
        });
    }
    public static void asyncSend(List<AbstractPacket> packets, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void asyncSend(AbstractPacket[] packets, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void asyncSend(AbstractPacket[] packets, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
    public static void asyncSend(List<AbstractPacket> packets, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for(AbstractPacket packet : packets){
                if(packet == null) continue;
                for(Player player : players) packet.sendPacket(player);
            }
        });
    }
}
