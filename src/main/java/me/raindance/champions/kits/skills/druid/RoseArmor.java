package me.raindance.champions.kits.skills.druid;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.Main;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Random;

@SkillMetadata(id = 209, skillType = SkillType.Druid, invType = InvType.DROP)
public class RoseArmor extends TogglePassive implements TimeResource, IEnergy {
    private final int damage = 2;
    private int a = 0;


    @Override
    public int getEnergyUsage() {
        return 40;
    }

    @Override
    public void toggle() {
        if(isToggled()) {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.RESISTANCE, Integer.MAX_VALUE, 2);
            run(1, 0);
        }else {
            unregister();
            StatusApplier.getOrNew(getPlayer()).removeStatus(Status.RESISTANCE);
        }
    }

    @Override
    public void task() {
        Location location = getPlayer().getLocation();
        location.getWorld().playSound(location, Sound.STEP_GRASS, 0.3f, 0.5f);
        spawnRose(location);
        useEnergy(getEnergyUsageTicks());
    }

    @Override
    public boolean cancel() {
        return !isToggled() || !hasEnergy(getEnergyUsageTicks()) || getChampionsPlayer().isSilenced();
    }

    @Override
    public void cleanup() {
        StatusApplier.getOrNew(getPlayer()).removeStatus(Status.RESISTANCE);
        if(!hasEnergy(getEnergyUsageTicks())) {
            forceToggle();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void hit(DamageApplyEvent event) {
        if(event.isCancelled()) return;
        if(!isToggled() || event.getVictim() != getPlayer()) return;
        if(event.getCause() != Cause.MELEE || event.getCause() != Cause.PROJECTILE) return;
        event.setDoKnockback(false);
        if(event.getCause() == Cause.MELEE) DamageApplier.damage(event.getAttacker(), getPlayer(), 2, this, false);

    }
    private void spawnRose(Location location) {
        a++;
        if(a % 2 != 0) return;
        a = 0;
        Random random = new Random();
        float randomizer = 0.1F * random.nextFloat();
        org.bukkit.util.Vector up = new Vector(0, 0.34, 0);
        up.setX(randomizer);
        up.setZ(randomizer);
        Item item = ItemManipulationManager.regular(Material.RED_ROSE, location, up);
        item.setCustomName("RITB");
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(getName() + item.getEntityId());
        item.getItemStack().setItemMeta(meta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, item::remove, 10);
    }
    @Override
    public String getName() {
        return "Rose Armor";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }
}
