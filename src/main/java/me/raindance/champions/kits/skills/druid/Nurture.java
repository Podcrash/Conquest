package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.Main;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import org.bukkit.Bukkit;
import org.bukkit.GrassSpecies;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Random;

@SkillMetadata(id = 205, skillType = SkillType.Druid, invType = InvType.DROP)
public class Nurture extends TogglePassive implements IEnergy, TimeResource {
    private int energyUsge = 30;
    private int a = 0;
    @Override
    public void toggle() {
        run(1, 0);
    }

    @Override
    public String getName() {
        return "Nurture";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public int getEnergyUsage() {
        return 30;
    }

    @Override
    public void task() {
        getGame().consumeBukkitPlayer(this::buff);
        useEnergy(getEnergyUsageTicks());
        spawnGrass(getPlayer().getLocation());
    }

    private void buff(Player victim) {
        if(victim != getPlayer() && !isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        StatusApplier.getOrNew(victim).applyStatus(Status.REGENERATION, 1.25f, 1, false, true);
    }

    @Override
    public boolean cancel() {
        return !isToggled() || !hasEnergy(getEnergyUsageTicks());
    }

    @Override
    public void cleanup() {
        if(!hasEnergy(getEnergyUsageTicks())) {
            forceToggle();
        }
    }

    private void spawnGrass(Location location) {
        a++;
        if(a % 2 != 0) return;
        a = 0;
        Random random = new Random();
        float randomizer = 0.1F * random.nextFloat();
        org.bukkit.util.Vector up = new Vector(0, 0.34, 0);
        up.setX(randomizer);
        up.setZ(randomizer);
        LongGrass data = new LongGrass();
        data.setSpecies(GrassSpecies.NORMAL);
        Item item = ItemManipulationManager.regular(data, location, up);
        item.setCustomName("RITB");
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(getName() + item.getEntityId());
        item.getItemStack().setItemMeta(meta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, item::remove, 10);
    }
}
