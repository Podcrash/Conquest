package me.raindance.champions.damage;

import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.Main;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public final class HitDetectionInjector {
    private static final float delay = 9.5F; //this is in ticks
    private HashMap<String, Long> delays = new HashMap<>();
    private Player player;

    public HitDetectionInjector(Player p) {
        this.player = p;
    }

    /**
     * Inject the custom hit detection to any user
     */
    public void injectHitDetection() {
        Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.instance, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
                EnumWrappers.EntityUseAction action = packet.getType();
                Entity entity = packet.getTarget(player.getWorld());
                if (!(action == EnumWrappers.EntityUseAction.ATTACK && entity instanceof Player)) return;
                Player victim = (Player) entity;
                Player attacker = event.getPlayer();
                if (attacker != player ||
                        attacker.getGameMode() == GameMode.SPECTATOR || victim.getGameMode() == GameMode.SPECTATOR) return;
                event.setCancelled(true);
                if (delays.containsKey(victim.getName())) {
                    long deltaTime = System.currentTimeMillis() - delays.get(victim.getName());
                    if ((deltaTime) < (delay / 20L) * 1000L) {
                        return;
                    }
                }
                delays.put(victim.getName(), System.currentTimeMillis());
                DamageApplier.damage(victim, attacker, findDamage((CraftPlayer) victim), true);
                //Bukkit.broadcastMessage(String.format("Damager: %s Victim: %s", player.getName(), entityPlayer.getName()));

            }
        });
    }

    /**
     * Find the amount of damage that the player can deal
     * This uses the original mineplex's strength/weakness system (+1/-1 respectively)
     * This means we are dividing out the 130% boost for strength, and etc
     * @param attacker - the player in question
     * @return the amount of damage
     */
    private double findDamage(CraftPlayer attacker) {
        double unfiltered = attacker.getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        for(PotionEffect effect : attacker.getActivePotionEffects()) {
            if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) unfiltered /= 1D + (1.3D * (effect.getAmplifier() + 1D));
            if(effect.getType().equals(PotionEffectType.WEAKNESS)) unfiltered += 0.5 * (effect.getAmplifier() + 1D);
        }

        return unfiltered;
    }

    public void injectSound() {
        Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {

                if (event.getPacket().getType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    PacketContainer packet = event.getPacket();
                    String soundName = packet.getStrings().getValues().get(0);
                    double x = packet.getIntegers().getValues().get(0) / 8.0d;
                    double y = packet.getIntegers().getValues().get(1) / 8.0d;
                    double z = packet.getIntegers().getValues().get(2) / 8.0d;
                    List<Float> volume = packet.getFloat().getValues();
                    List<Integer> pitch = packet.getIntegers().getValues();

                    if (soundName.equals("game.player.hurt")) {
                        player.getWorld().playSound(new Location(player.getWorld(), x, y, z), Sound.BLAZE_HIT, 0.5f, 1f);
                        event.setCancelled(true);
                        //packet.getFloat().getValues().set(1, 0.5f);
                    } else {
                        Main.instance.getLogger().info(String.format("Sound played: %s Volume: %s Pitch: %s", soundName, volume, pitch));
                        player.sendMessage(String.format("Sound played: %s Volume: %s Pitch: %s", soundName, volume, pitch));
                    }

                } else if (event.getPacket().getType() == PacketType.Play.Server.CUSTOM_SOUND_EFFECT) {
                    PacketContainer packet = event.getPacket();
                    player.sendMessage("---------------------------");
                    player.sendMessage(packet.toString());
                }
            }
        });
    }

    public void injectFindAllPackets() {

    }
}
