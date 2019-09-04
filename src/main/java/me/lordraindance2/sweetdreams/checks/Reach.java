package me.lordraindance2.sweetdreams.checks;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.lordraindance2.sweetdreams.LunarDance;
import me.lordraindance2.sweetdreams.tracker.CoordinateTracker;
import me.lordraindance2.sweetdreams.util.BoundingBox;
import me.lordraindance2.sweetdreams.util.Coordinate;
import me.lordraindance2.sweetdreams.util.RayTracer;
import me.lordraindance2.sweetdreams.violation.ViolationType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Reach extends Check {
    public static double accuracy = 0.99;
    public Reach(Player player) {
        super(CheckType.REACH, player,
                PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void recieve(PacketContainer packet) {
        EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
        if(action != EnumWrappers.EntityUseAction.ATTACK) return;
        evaluateIfReach(packet);
    }

    public void evaluateIfReach(PacketContainer packet) {
        Entity target = packet.getEntityModifier(getPlayer().getWorld()).read(0);
        //We are testing for reach, so if the distance (squared) is close enough,
        //then that's not reach
        if(getPlayer().getLocation().distanceSquared(target.getLocation()) <= 4) return;

        CoordinateTracker tracker = LunarDance.getCoordinateTracker();
        //victim's hitbox
        BoundingBox box = Coordinate.fromEntity(target).getBoundingBox(target);
        //Where the attacker is looking and his location
        Coordinate lastCoordinate = tracker.getHead(getPlayer());
        //Be a bit closer as well
        double y = lastCoordinate.getY();
        lastCoordinate = lastCoordinate
                .add(Coordinate.fromVector(lastCoordinate.getDirection())
                        .multiply(0.05)).setY(y);
        //Make a tracer
        RayTracer rayTrace = new RayTracer(lastCoordinate.toVector(), lastCoordinate.getDirection());

        Vector vector = rayTrace.positionOfIntersection(box, 4, accuracy);
        if(!rayTrace.intersectsBoundingBox(box, 3, accuracy)) {
            tellPlayersWithLog(ViolationType.SERIOUS, null);
            flag();
        }
    }


    @Override
    public void flag() {

    }

    @Override
    public void send(PacketContainer packet) {

    }

}
