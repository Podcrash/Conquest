package me.raindance.champions.injectors;

import com.packetwrapper.abstractpackets.WrapperPlayServerEntityStatus;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import me.raindance.champions.Main;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.sound.SoundPlayer;
import org.bukkit.entity.Player;

public class SoundInjector extends InjectorBase {
    public SoundInjector() {
        super(ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.NAMED_SOUND_EFFECT);
        Main.getInstance().log.info(getClass().getSimpleName() + ": Injecting.");
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Server.ENTITY_STATUS) {
            WrapperPlayServerEntityStatus status = new WrapperPlayServerEntityStatus(event.getPacket());
            if (!(status.getEntity(event) instanceof Player)) return;
            Player player = (Player) status.getEntity(event);
            KitPlayer kitPlayer = KitPlayerManager.getInstance().getKitPlayer(player);
            if (kitPlayer == null) return;
            SoundPlayer.sendSound(player.getLocation(), kitPlayer.getSound());
        }else {
            if(event.getPacket().getStrings().getValues().get(0).equals("game.player.hurt")) event.setCancelled(true);
        }
    }
}
