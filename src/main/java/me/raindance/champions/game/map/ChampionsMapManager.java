package me.raindance.champions.game.map;

import com.podcrash.api.mc.map.JsonHelper;
import com.podcrash.api.mc.map.MapManager;
import me.raindance.champions.Main;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ChampionsMapManager {
    private Main mstance;
    //TODO: Refactor using the new configurator
    private static volatile ChampionsMapManager championsMapManager;
    private static final String[] xyzs = new String[]{".x", ".y", ".z"};

    private ChampionsMapManager() {
        if (championsMapManager != null) {
            throw new RuntimeException("Please use getInstance() method");
        }
        mstance = Main.getInstance();
    }

    public static ChampionsMapManager getInstance() {
        if (championsMapManager == null) {
            synchronized (ChampionsMapManager.class) {
                if (championsMapManager == null) {
                    championsMapManager = new ChampionsMapManager();
                }
            }

        }
        return championsMapManager;
    }
    /**
     * This is important - this is where the objective is registered
     * 1st line - sign.get(0) = mapname
     * 2nd line - sign.get(1) = objectivetype (CapturePoint, Emerald, Restock, and Flag are valid responses)
     * 3rd line - sign.get(2) = if it is a capture point, give a name, (if it's not, this side will be ignored)
     * 4th line - sign.get(3) = soonTM - side of which the items will come out of
     * All of these are case-insensitive (ignores case) except for the 3rd line
     */
    public boolean registerObjective(Sign sign) {
        String mapName = sign.getLine(0).toLowerCase();
        String objectiveType = sign.getLine(1).toLowerCase();
        String objectiveName = (objectiveType.equals("capturepoint") && sign.getLines().length >= 2) ? sign.getLine(2) : null;

        Location location = sign.getLocation();
        if (objectiveName == null || objectiveName.equalsIgnoreCase("")) return false;

        location.subtract(0, 2, 0);
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        CapturePoint capturePoint = new CapturePoint(objectiveName, location.toVector());
        if(!capturePoint.validate(Bukkit.getWorld(mapName))) return false;
        MapManager.appendObject(mapName, "capturePoints", objectiveName, JsonHelper.wrapXYZ(new double[]{x,y,z}));
        return true;
    }

    public boolean registerEmerald(Block block) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
        World world = block.getWorld();
        MapManager.insertNext(world.getName(), "emeralds", new double[]{x,y,z});
        return true;
    }

    public boolean registerRestock(Block block) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
        World world = block.getWorld();
        MapManager.insertNext(world.getName(), "restocks", new double[]{x,y,z});
        return true;
    }

    public boolean registerSpawn(Player p, Block block, Wool wool) {
        if (wool == null) return false;
        if (!wool.getColor().equals(DyeColor.BLUE) && !wool.getColor().equals(DyeColor.RED)) return false;
        int index = wool.getColor().equals(DyeColor.BLUE) ? 1 : 0;
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
        MapManager.insert(block.getWorld().getName(), "spawns", index, new double[]{x,y,z});
        return true;
    }
}
