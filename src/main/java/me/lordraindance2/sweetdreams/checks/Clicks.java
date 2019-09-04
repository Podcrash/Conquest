package me.lordraindance2.sweetdreams.checks;

import com.comphenix.packetwrapper.WrapperPlayClientArmAnimation;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Clicks extends Check {
    private long timeSinceLast;
    public Clicks(Player player) {
        super(CheckType.CLICKS, player, PacketType.Play.Client.ARM_ANIMATION);
        this.timeSinceLast = System.currentTimeMillis();
    }

    @Override
    public void recieve(PacketContainer packet) {
        long delta = System.currentTimeMillis() - timeSinceLast;
        this.timeSinceLast = System.currentTimeMillis();
        Bukkit.broadcastMessage(getPlayer().getName() + ": LastClick: " + delta);
    }

    @Override
    public void send(PacketContainer packet) {

    }

    @Override
    public void flag() {

    }
    private boolean isDigging() {
        Vector vector = getPlayer().getLocation().getDirection();
        return true;
    }
}
