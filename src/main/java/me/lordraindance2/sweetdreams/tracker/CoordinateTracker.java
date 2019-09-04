package me.lordraindance2.sweetdreams.tracker;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.reflect.StructureModifier;
import me.lordraindance2.sweetdreams.util.Coordinate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * The class is needed because it needs to get the most accurate position possible
 * by the player.
 */
public final class CoordinateTracker implements IPlayerTrack<Coordinate> {
    private JavaPlugin plugin;
    private static final List<PacketType> POSITION_TYPES = Arrays.asList(
            PacketType.Play.Client.FLYING,
            PacketType.Play.Client.POSITION_LOOK,
            PacketType.Play.Client.POSITION,
            PacketType.Play.Client.LOOK);

    private PacketListener listener;
    private Map<String, ArrayDeque<Coordinate>> lastTimes;
    public CoordinateTracker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lastTimes = new HashMap<>();
    }

    @Override
    public void enable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                listener = new PacketAdapter(plugin, ListenerPriority.LOWEST, POSITION_TYPES) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                recieve(event);
            }
        });
    }
    @Override
    public void disable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
    }

    @Override
    public Coordinate get(Player player) {
        return get(player.getName());
    }
    /**
     * Just accounts for the head height
     * {@link me.lordraindance2.sweetdreams.checks.Reach#evaluateIfReach(PacketContainer)}
     * @param player
     * @return
     */
    public Coordinate getHead(Player player) {
        return get(player).add(0, player.getEyeHeight(), 0);
    }

    /**
     * Finds the last position updated by the player.
     * @param name the player name
     * @return the last coordinate the player was in.
     */
    private Coordinate get(String name) {
        return lastTimes.get(name).getLast();
    }

    private void recieve(PacketEvent event) {
        recordLocation(event.getPlayer(), event.getPacket());
    }

    /**
     * Finds the current position from the position packets and stores them.
     * This used 4 packets because sometimes the player doesn't:
     * a) move
     * b) aim
     * c) or both of the above at the same time.
     *
     * Flying covers not moving and not aiming
     * Position-look covers both moving and aiming
     * Position covers moving and not aiming
     * Look covers not moving and aiming
     */
    private void recordLocation(Player player, PacketContainer packet) {
        double x = 0, y = 0, z = 0;
        float yaw = 0, pitch = 0;

        boolean ground;
        if(packet.getType() == PacketType.Play.Client.FLYING) {
            Location location = player.getLocation();
            x = location.getX();
            y = location.getY();
            z = location.getZ();

            yaw = location.getYaw();
            pitch = location.getPitch();

            ground = true;
        }else {
            ground = packet.getBooleans().read(0);
            if(packet.getType() == PacketType.Play.Client.POSITION_LOOK) {
                StructureModifier<Double> doubles = packet.getDoubles();
                x = doubles.read(0);
                y = doubles.read(1);
                z = doubles.read(2);

                StructureModifier<Float> floats = packet.getFloat();
                yaw = floats.read(0);
                pitch = floats.read(1);

            }else {
                Location location = player.getLocation();
                if(packet.getType() == PacketType.Play.Client.POSITION) {
                    StructureModifier<Double> doubles = packet.getDoubles();
                    x = doubles.read(0);
                    y = doubles.read(1);
                    z = doubles.read(2);

                    yaw = location.getYaw();
                    pitch = location.getPitch();

                }else if(packet.getType() == PacketType.Play.Client.LOOK) {
                    x = location.getX();
                    y = location.getY();
                    z = location.getZ();

                    StructureModifier<Float> floats = packet.getFloat();
                    yaw = floats.read(0);
                    pitch = floats.read(1);
                }
            }
        }

        //1.62 accounts for the head
        //old comment ^ just use the normal head height method so that sneaking
        //doesn't make false positives
        //y += player.getEyeHeight();
        Coordinate newUpdate = new Coordinate(x, y, z, yaw, pitch, ground);
        add(player, newUpdate);
    }

    /**
     * Only store the last 3 locations
     * @param player the player to add to the map
     * @param coordinate the coordinate to add
     */
    private void add(Player player, Coordinate coordinate) {
        if(lastTimes.get(player.getName()) == null) lastTimes.put(player.getName(), new ArrayDeque<>());
        ArrayDeque<Coordinate> lastCoords = lastTimes.get(player.getName());
        lastCoords.addLast(coordinate);
        if(lastCoords.size() > 3)
            lastCoords.removeFirst();
    }
}
