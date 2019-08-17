package me.raindance.champions.game.objects.objectives;

import me.raindance.champions.game.objects.ItemObjective;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class Emerald extends ItemObjective {
    private static ObjectiveType otype = ObjectiveType.EMERALD;
    public Emerald(Location spawnLocation){
        super(Material.EMERALD, Material.EMERALD_BLOCK, spawnLocation);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.BALL).build();
    }
    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Emerald";
    }
}
