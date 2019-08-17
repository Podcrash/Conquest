package me.raindance.champions.injectors;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import me.raindance.champions.Main;

public class InjectorBase extends PacketAdapter {
    public InjectorBase(ListenerPriority listenerPriority, PacketType... types) {
        super(Main.instance, listenerPriority, types);
        Main.getInstance().getProtocolManager().addPacketListener(this);
    }
}
