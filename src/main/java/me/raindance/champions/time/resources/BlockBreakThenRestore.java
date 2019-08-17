package me.raindance.champions.time.resources;

import me.raindance.champions.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;

public final class BlockBreakThenRestore {
    private long duration;
    private final Material material1;
    private final Location location;

    public BlockBreakThenRestore(int duration, Material material1, Location location) {
        this.duration = System.currentTimeMillis() + duration * 1000L;
        this.material1 = material1;
        this.location = location;
    }

    public boolean check() {
        return System.currentTimeMillis() >= duration;
    }

    public void remove() {
        BlockUtil.setBlock(location, material1, (byte) 0);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = System.currentTimeMillis() + duration * 1000L;
    }

    public Material getMaterial1() {
        return material1;
    }

    public Location getLocation() {
        return location;
    }
}
