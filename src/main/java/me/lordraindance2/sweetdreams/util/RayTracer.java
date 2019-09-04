package me.lordraindance2.sweetdreams.util;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class RayTracer {
    public static double DEFAULT_ESTIMATE = 0.12;
    private Vector origin, direction;

    public RayTracer(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public boolean intersectsBoundingBox(BoundingBox box, double distance, double accuracy) {
        assert accuracy > 0 && accuracy < 1;
        double add = 1D - accuracy;
        for(double i = 0; i <= distance; i += add) {
            Vector originClone = origin.clone();
            Vector addVector = direction.clone();

            originClone.add(addVector.multiply(i));
            particles(Bukkit.getWorld("Gulley"), originClone);
            if(intersectsBox(originClone, box)) return true;
        }
        return false;
    }

    public Vector positionOfIntersection(BoundingBox box, double distance, double accuracy) {
        assert accuracy > 0 && accuracy < 1;
        double add = 1D - accuracy;
        for(double i = 0; i <= distance; i += add) {
            Vector originClone = origin.clone();
            Vector addVector = direction.clone();

            originClone.add(addVector.multiply(i));
            if(intersectsBox(originClone, box)) return originClone;
        }
        return null;
    }

    public boolean intersectsBox(Vector vector, BoundingBox box) {
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        double minX = box.min.getX();
        double minY = box.min.getY();
        double minZ = box.min.getZ();

        double maxX = box.max.getX();
        double maxY = box.max.getY();
        double maxZ = box.max.getZ();

        if(compare(minX, x, maxX)) {
            if(compare(minY, y, maxY)) {
                if(compare(minZ, z, maxZ)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean compare(double a, double b, double c) {
        return compare(a, b, c, DEFAULT_ESTIMATE);
    }

    /**
     * Find whether or not: b is in between a or c.
     * Estimate is the point of negligence.
     * @param a min
     * @param b value to test
     * @param c max
     * @param estimate offset
     * @return the
     */
    private boolean compare(double a, double b, double c, double estimate) {
        //First condition: keep old behavior
        //Second condition: allow some negligence
        return (a <= b && b <= c) || (a - estimate < b && b < c + estimate);
    }

    private void particles(World world, Vector vector) {
        world.playEffect(vector.toLocation(world), Effect.COLOURED_DUST, 0);
    }
}
