package me.raindance.champions.damage;

import com.abstractpackets.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.Main;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public final class HitDetectionInjector {
    private static HashMap<String, HitDetectionInjector> injectors = new HashMap<>();
    public static long delay = 400; //this is in ticks
    private PacketListener listener;
    private HashMap<String, Long> delays = new HashMap<>();
    private HashMap<String, Long> deathDelay = new HashMap<>();
    private Player player;

    public static HitDetectionInjector getHitDetection(Player p) {
        return injectors.get(p.getName());
    }
    public HitDetectionInjector(Player p) {
        this.player = p;
        this.listener = new PacketAdapter(Main.instance, ListenerPriority.HIGH, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
                EnumWrappers.EntityUseAction action = packet.getType();
                Entity entity = packet.getTarget(player.getWorld());
                if (!(action == EnumWrappers.EntityUseAction.ATTACK && entity instanceof Player)) return;
                LivingEntity victim = (LivingEntity) entity;
                Player attacker = event.getPlayer();
                if (attacker != player ||
                        attacker.getGameMode() == GameMode.SPECTATOR || (victim instanceof Player && ((Player) victim).getGameMode() == GameMode.SPECTATOR)) return;
                event.setCancelled(true);
                if (delays.containsKey(victim.getName())) {
                    long deltaTime = System.currentTimeMillis() - delays.get(victim.getName());
                    if ((deltaTime) < delay)
                        return;
                }
                delays.put(victim.getName(), System.currentTimeMillis());
                if(!isDeathDelay(victim) && !isInvis(victim) && !player.isBlocking()) {
                    double damage = findDamage(attacker);
                    DamageApplier.damage(victim, attacker, damage, true);
                    if(victim instanceof Player && calculateIfDeath(damage, victim)) {
                        Player playerVictim = (Player) victim;
                        manualDeathDelay(playerVictim);
                        getHitDetection(playerVictim).manualDeathDelay(player);
                    }
                }
                //Bukkit.broadcastMessage(String.format("Damager: %s Victim: %s", player.getName(), entityPlayer.getName()));

            }
        };
        injectors.put(p.getName(), this);
    }

    /**
     * Inject the custom hit detection to any user
     */
    public void injectHitDetection() {
        Main.getInstance().getProtocolManager().addPacketListener(listener);
    }

    public void deinject() {
        Main.getInstance().getProtocolManager().removePacketListener(listener);
        injectors.remove(player.getName());
    }

    public void manualDeathDelay(Player player) {
        deathDelay.put(player.getName(), System.currentTimeMillis() + delay);
    }
    private boolean isDeathDelay(LivingEntity player) {
        long time = deathDelay.getOrDefault(player.getName(), -1L);
        return time != -1L && System.currentTimeMillis() < time;
    }
    private boolean isInvis(LivingEntity player) {
        if(!(player instanceof Player)) return false;
        ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer((Player) player);
        return cPlayer != null && cPlayer.isCloaked();
    }

    private boolean calculateIfDeath(double damage, LivingEntity victim) {
        EntityLiving entityLiving = ((CraftLivingEntity) victim).getHandle();
        int aV = entityLiving.br();
        double damageFormula = damage * (1D - 0.04D * aV);
        return victim.getHealth() - damageFormula <= 0;
    }
    /**
     * Find the amount of damage that the player can deal
     * This uses the original mineplex's strength/weakness system (+1/-1 respectively)
     * This means we are dividing out the 130% boost for strength, and etc
     * @param attacker - the player in question
     * @return the amount of damage
     */
    private double findDamage(LivingEntity attacker) {
        double unfiltered = ((CraftLivingEntity) attacker).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        for(PotionEffect effect : attacker.getActivePotionEffects()) {
            if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) unfiltered /= 1D + (1.3D * (effect.getAmplifier() + 1D));
            if(effect.getType().equals(PotionEffectType.WEAKNESS)) unfiltered += 0.5 * (effect.getAmplifier() + 1D);
        }

        return unfiltered - 1;
    }

    public void injectSound() {
        Main.getInstance().getProtocolManager().addPacketListener(new PacketAdapter(Main.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if(event.getPlayer() != player) return;
                if (event.getPacket().getType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    PacketContainer packet = event.getPacket();
                    String soundName = packet.getStrings().getValues().get(0);
                    double x = packet.getIntegers().getValues().get(0) / 8.0d;
                    double y = packet.getIntegers().getValues().get(1) / 8.0d;
                    double z = packet.getIntegers().getValues().get(2) / 8.0d;
                    List<Float> volume = packet.getFloat().getValues();
                    List<Integer> pitch = packet.getIntegers().getValues();

                    Main.instance.getLogger().info(String.format("Sound played: %s Volume: %s Pitch: %s", soundName, volume, pitch));
                    Bukkit.broadcastMessage(String.format("Sound played: %s Volume: %s Pitch: %s", soundName, volume, pitch));

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
