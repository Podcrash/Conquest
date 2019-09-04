package me.lordraindance2.sweetdreams.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Iterables;
import me.lordraindance2.sweetdreams.JoinInject;
import me.lordraindance2.sweetdreams.LunarDance;
import me.lordraindance2.sweetdreams.violation.Violation;
import me.lordraindance2.sweetdreams.violation.ViolationType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class Check {
    private JavaPlugin plugin;
    private Player player;
    private PacketType[] packetTypes;
    private CheckType checkType;
    private PacketListener packetListener;

    private boolean disabled;

    public Check(CheckType checkType, Player player, PacketType... types) {
        this.plugin = LunarDance.plugin;
        this.checkType = checkType;
        this.player = player;
        this.packetTypes = types;
        this.disabled = true;
    }
    public Check(CheckType checkType, Player player, Iterable<PacketType> types) {
        this(checkType, player);
        List<PacketType> typeList = new ArrayList<>();
        Iterables.addAll(typeList, types);
        this.packetTypes = typeList.toArray(new PacketType[typeList.size()]);
    }

    public abstract void recieve(PacketContainer packet);
    public abstract void send(PacketContainer packet);
    public abstract void flag();

    public void inject() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                packetListener = new PacketAdapter(plugin, ListenerPriority.MONITOR, packetTypes) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(!event.isCancelled() && event.getPlayer() == player)
                    recieve(event.getPacket());
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                send(event.getPacket());
            }
        });
    }

    public void deinject() {
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
    }
    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
        return player;
    }
    public WrappedGameProfile getProfile() {
        return WrappedGameProfile.fromPlayer(player);
    }
    public PacketType[] getPacketTypes() {
        return packetTypes;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public PacketListener getPacketListener() {
        return packetListener;
    }

    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected void log(String msg) {
        if(disabled) return;
        plugin.getLogger().info(String.format("[%s]: ", this.getClass().getSimpleName()) + msg);
    }
    protected String getFlagMessage(ViolationType violationType, String extra){
        String offender = getPlayer().getName();
        String current = ChatColor.MAGIC + "Cheats " + ChatColor.RESET + ChatColor.GREEN.toString() + offender + ChatColor.YELLOW + " flagged " + ChatColor.DARK_AQUA + '[' + violationType.name() + ']' + ChatColor.YELLOW +  " for " + ChatColor.GOLD + checkType.getName();
        return extra == null ? current : current + '\n' + ChatColor.RED + "Extra details: " + extra;
    }
    protected void tellPlayersWithLog(ViolationType violationType, String extra) {
        for(Player player : JoinInject.getOnlinePlayers()) {
            if(player.isOp() ||
                    (!player.hasPermission("Champions.developer") && !player.hasPermission("Champions.anticheat"))) continue;
            player.sendMessage(getFlagMessage(violationType, extra));
        }
    }
}
