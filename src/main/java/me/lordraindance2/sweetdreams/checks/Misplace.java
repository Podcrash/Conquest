package me.lordraindance2.sweetdreams.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import me.lordraindance2.sweetdreams.util.Coordinate;
import org.bukkit.entity.Player;

public class Misplace extends Check {
    private Coordinate lastSent;
    private boolean sent;
    private int ticks;
    public Misplace(Player player) {
        super(CheckType.MISPLACE, player, PacketType.Play.Client.FLYING, PacketType.Play.Server.ENTITY_TELEPORT);

    }

    @Override
    public void recieve(PacketContainer packet) {
        ticks++;
        if(!this.sent) return;

        this.sent = false;
    }

    @Override
    public void send(PacketContainer packet) {
        //lastSent = readTeleportPacket(packet);
        this.sent = true;
    }

    @Override
    public void flag() {

    }

    private Coordinate readTeleportPacket(PacketContainer packet) {
        StructureModifier<Double> doubles = packet.getDoubles();
        double x = doubles.read(0);
        double y = doubles.read(1);
        double z = doubles.read(2);

        StructureModifier<Float> angles = packet.getFloat();
        float yaw = angles.read(0);
        float pitch = angles.read(1);

        boolean ground = packet.getBooleans().read(0);
        return new Coordinate(x, y, z);
    }
}
