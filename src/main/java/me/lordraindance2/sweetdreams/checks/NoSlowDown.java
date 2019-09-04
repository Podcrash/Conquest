package me.lordraindance2.sweetdreams.checks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.lordraindance2.sweetdreams.util.Coordinate;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NoSlowDown extends Check {
    private EntityPlayer entityPlayer;
    private Coordinate lastCoord;

    public NoSlowDown(Player player) {
        super(CheckType.NOSLOWDOWN, player,
                PacketType.Play.Client.POSITION_LOOK,
                PacketType.Play.Client.POSITION);
        this.entityPlayer = ((CraftPlayer) player).getHandle();
    }

    @Override
    public void recieve(PacketContainer packet) {
        double curr = entityPlayer.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
        double x = packet.getDoubles().read(0);
        double y = packet.getDoubles().read(1);
        double z = packet.getDoubles().read(2);

        Coordinate current = new Coordinate(x, y, z);
        if(lastCoord == null) lastCoord = current;
        Coordinate delta = current.subtract(lastCoord);

        //if the player didn't move, don't do anything
        if(delta.distanceSquared() == 0) return;
        this.lastCoord = current;

        log(delta.toString());
    }

    @Override
    public void send(PacketContainer packet) {

    }

    @Override
    public void flag() {

    }

}
