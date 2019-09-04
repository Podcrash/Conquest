package me.lordraindance2.sweetdreams.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class General extends Check {
    public General(Player player) {
        super(CheckType.TEST, player, PacketType.values());
    }

    @Override
    public void recieve(PacketContainer packet) {
        log(packet.getType() + " ");
    }

    @Override
    public void send(PacketContainer packet) {

    }

    @Override
    public void flag() {

    }
}
