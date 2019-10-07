package me.raindance.champions.injectors;

import com.abstractpackets.packetwrapper.WrapperPlayServerEntityStatus;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import me.raindance.champions.Main;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import com.podcrash.api.mc.sound.SoundPlayer;
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
            ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
            if (championsPlayer == null) return;
            SoundPlayer.sendSound(player.getLocation(), championsPlayer.getSound());
        }else {
            if(event.getPacket().getStrings().getValues().get(0).equals("game.player.hurt")) event.setCancelled(true);
        }
    }
}
