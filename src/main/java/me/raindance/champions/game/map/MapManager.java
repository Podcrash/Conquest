package me.raindance.champions.game.map;

import me.raindance.champions.Main;
import me.raindance.champions.game.objects.objectives.CapturePoint;
import me.raindance.champions.game.objects.objectives.Emerald;
import me.raindance.champions.game.objects.objectives.Restock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    //TODO: Refactor using the new configurator
    private static volatile MapManager mapManager;
    private Main mstance = Main.getInstance();
    private static final String[] xyzs = new String[]{".x", ".y", ".z"};

    private MapManager() {
        if (mapManager != null) {
            throw new RuntimeException("Please use getInstance() method");
        }
    }

    public static MapManager getInstance() {
        if (mapManager == null) {
            synchronized (MapManager.class) {
                if (mapManager == null) {
                    mapManager = new MapManager();
                }
            }

        }
        return mapManager;
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
        World world = location.getWorld();
        if (objectiveName == null ||
                !mapName.equalsIgnoreCase(world.getName()) ||
                objectiveName.equalsIgnoreCase("")) {
            return false;
        } else {
            location.subtract(0, 2, 0);
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            CapturePoint capturePoint = new CapturePoint(objectiveName, location);
            if(!capturePoint.validate()) return false;
            if (mstance.getMapConfiguration().isSet(world.getName() + ".capturepoints." + objectiveName + ".x") &&
                    mstance.getMapConfiguration().isSet(world.getName() + ".capturepoints." + objectiveName + ".y") &&
                    mstance.getMapConfiguration().isSet(world.getName() + ".capturepoints." + objectiveName + ".z")) {
                mstance.getMapConfiguration().set(world.getName() + ".capturepoints." + objectiveName, null);
            } else {
                mstance.getMapConfiguration().set(world.getName() + ".capturepoints." + objectiveName + ".x", x);
                mstance.getMapConfiguration().set(world.getName() + ".capturepoints." + objectiveName + ".y", y);
                mstance.getMapConfiguration().set(world.getName() + ".capturepoints." + objectiveName + ".z", z);
            }
            mstance.saveMapConfig();
            return true;
        }
    }

    public boolean registerEmerald(Block block) {
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
        World world = block.getWorld();
        try {
            int i = 0;
            while (true) {
                if (mstance.getMapConfiguration().isSet(world.getName() + ".emerald." + i)) {
                    i++;
                } else {
                    if (mstance.getMapConfiguration().isSet(world.getName() + ".emerald." + i + ".x." + x) &&
                            mstance.getMapConfiguration().isSet(world.getName() + ".emerald." + i + ".y." + y) &&
                            mstance.getMapConfiguration().isSet(world.getName() + ".emerald." + i + ".z." + z)) {
                        mstance.getMapConfiguration().set(world.getName() + ".emerald." + i, null);
                    } else {
                        mstance.getMapConfiguration().set(world.getName() + ".emerald." + i + ".x", x);
                        mstance.getMapConfiguration().set(world.getName() + ".emerald." + i + ".y", y);
                        mstance.getMapConfiguration().set(world.getName() + ".emerald." + i + ".z", z);

                    }
                    mstance.saveMapConfig();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerRestock(Block block) {
        Main mstance = Main.getInstance();
        double x = block.getX();
        double y = block.getY();
        double z = block.getZ();
        World world = block.getWorld();
        try {
            int i = 0;
            while (true) {
                if (mstance.getMapConfiguration().isSet(world.getName() + ".restock." + i)) {
                    i++;
                } else {
                    if (mstance.getMapConfiguration().isSet(world.getName() + ".restock." + i + ".x") &&
                            mstance.getMapConfiguration().isSet(world.getName() + ".restock." + i + ".y") &&
                            mstance.getMapConfiguration().isSet(world.getName() + ".restock." + i + ".z")) {
                        mstance.getMapConfiguration().set(world.getName() + ".restock." + i, null);
                    } else {
                        mstance.getMapConfiguration().set(world.getName() + ".restock." + i + ".x", x);
                        mstance.getMapConfiguration().set(world.getName() + ".restock." + i + ".y", y);
                        mstance.getMapConfiguration().set(world.getName() + ".restock." + i + ".z", z);
                    }
                    mstance.saveMapConfig();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerSpawn(Player p, Block block, Wool wool) {
        if (wool == null) return false;
        if (!wool.getColor().equals(DyeColor.BLUE) && !wool.getColor().equals(DyeColor.RED)) return false;

        if (block.getLocation().add(0, 1, 0).getBlock().getType().equals(Material.IRON_PLATE)) {
            try {
                World world = block.getWorld();
                String color = wool.getColor().equals(DyeColor.BLUE) ? "blue" : "red";
                String partPath = "." + color + "spawn.";
                double x = block.getX();
                double y = block.getY();
                double z = block.getZ();
                int i = 0;
                while (true) {
                    if (mstance.getMapConfiguration().isSet(world.getName() + partPath + i)) {
                        double sx = mstance.getMapConfiguration().getDouble(world.getName() + partPath + i + ".x");
                        double sy = mstance.getMapConfiguration().getDouble(world.getName() + partPath + i + ".y");
                        double sz = mstance.getMapConfiguration().getDouble(world.getName() + partPath + i + ".z");
                        if(sx == x && sy == y && z == sz){
                            mstance.getMapConfiguration().set(world.getName() + partPath + i, null);
                            continue;
                        }
                        i++;
                    } else {
                        if (mstance.getMapConfiguration().isSet(world.getName() + partPath + i + ".x." + x) &&
                                mstance.getMapConfiguration().isSet(world.getName() + partPath + i + ".y." + y) &&
                                mstance.getMapConfiguration().isSet(world.getName() + partPath + i + ".z." + z)) {
                            mstance.getMapConfiguration().set(world.getName() + partPath + i, null);
                        } else {
                            mstance.getMapConfiguration().set(world.getName() + partPath + i + ".x", x);
                            mstance.getMapConfiguration().set(world.getName() + partPath + i + ".y", y);
                            mstance.getMapConfiguration().set(world.getName() + partPath + i + ".z", z);

                        }
                        mstance.saveMapConfig();
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    public List<Location> loadRedSpawns(World world, String worldName) {
        int i = 0;
        ConfigurationSection section = mstance.getMapConfiguration().getConfigurationSection(worldName + ".redspawn");
        List<Location> redSpawns = new ArrayList<>();
        while (true) {
            if (section.isSet(String.valueOf(i))) {
                double x = section.getDouble(i + ".x");
                double y = section.getDouble(i + ".y");
                double z = section.getDouble(i + ".z");
                redSpawns.add(new Location(world, x, y, z));
                i++;
            } else return redSpawns;
        }
    }

    public List<Location> loadBlueSpawns(World world, String worldName) {
        int i = 0;
        ConfigurationSection section = mstance.getMapConfiguration().getConfigurationSection(worldName + ".bluespawn");
        List<Location> blueSpawns = new ArrayList<>();
        while (true) {
            if (section.isSet(String.valueOf(i))) {
                double x = section.getDouble(i + ".x");
                double y = section.getDouble(i + ".y");
                double z = section.getDouble(i + ".z");
                blueSpawns.add(new Location(world, x, y, z));
                i++;
            } else return blueSpawns;
        }
    }

    public List<Emerald> loadEmeralds(World world, String worldName) {
        int i = 0;
        ConfigurationSection section = mstance.getMapConfiguration().getConfigurationSection(worldName + ".emerald");
        List<Emerald> emeralds = new ArrayList<>();
        while (true) {
            if (section.isSet(i + "")) {
                double x = section.getDouble(i + ".x");
                double y = section.getDouble(i + ".y");
                double z = section.getDouble(i + ".z");
                emeralds.add(new Emerald(new Location(world, x, y, z)));
                i++;
            } else return emeralds;
        }
    }

    public List<Restock> loadRestocks(World world, String worldName) {
        int i = 0;
        ConfigurationSection section = mstance.getMapConfiguration().getConfigurationSection(worldName + ".restock");
        List<Restock> restocks = new ArrayList<>();
        while (true) {
            if (section.isSet(String.valueOf(i))) {
                double x = section.getDouble(i + ".x");
                double y = section.getDouble(i + ".y");
                double z = section.getDouble(i + ".z");
                restocks.add(new Restock(new Location(world, x, y, z)));
                i++;
            } else return restocks;
        }
    }

    public List<CapturePoint> loadCapturePoints(World world, String worldName) {
        ConfigurationSection section = mstance.getMapConfiguration().getConfigurationSection(worldName + ".capturepoints");
        List<CapturePoint> capturePoints = new ArrayList<>();
        for (String s : section.getKeys(false)) {
            double x = section.getDouble(s + ".x");
            double y = section.getDouble(s + ".y");
            double z = section.getDouble(s + ".z");
            capturePoints.add(new CapturePoint(s, new Location(world, x, y, z)));
        }
        return capturePoints;
    }

    public void registerFlag() {

    }

    private boolean verifyCapturePoint() {
        return false;
    }
}
