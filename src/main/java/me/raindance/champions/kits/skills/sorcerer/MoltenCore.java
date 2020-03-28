package me.raindance.champions.kits.skills.sorcerer;

import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SkillMetadata(id = 1008, skillType = SkillType.Sorcerer, invType = InvType.PASSIVEA)
public class MoltenCore extends TogglePassive implements IEnergy, TimeResource, IConstruct {
    private final int MAX_LEVEL = 1;
    private final Vector up = new Vector(0, 0.34, 0);
    private int energy = 20;
    private String NAME;

    private byte a = 0;
    public MoltenCore(){}

    @Override
    public void afterConstruction() {
        NAME = (getPlayer() == null) ? null : getPlayer().getName() + getName();
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }

    @Override
    public String getName() {
        return "Molten Core";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public void toggle() {
        run(1, 0);
    }

    private void buff(Location location) {
        StatusApplier playerApplier = StatusApplier.getOrNew(getPlayer());
        if(getPlayer().getFireTicks() > 0)
            playerApplier.removeStatus(Status.FIRE);
        playerApplier.applyStatus(Status.SPEED, 0.5f, 0);

        List<Player> players = BlockUtil.getPlayersInArea(location, 6, getPlayers());
        for(Player p : players) {
            if(!isAlly(p) || p != getPlayer()) continue;
            StatusApplier.getOrNew(p).applyStatus(Status.FIRE_RESISTANCE, 1, 4);
        }
    }

    private void spawnFire(Location location) {
        a++;
        if(a % 2 != 0) return;
        a = 0;
        Random random = new Random();
        float randomizer = 0.1F * random.nextFloat();
        up.setX(randomizer);
        up.setZ(randomizer);
        Item item = ItemManipulationManager.regular(Material.BLAZE_POWDER, location, up);
        item.setCustomName("RITB");
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(NAME + item.getEntityId());
        item.getItemStack().setItemMeta(meta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, item::remove, 10);
    }
    @Override
    public void task() {
        Location location = getPlayer().getLocation();

        location.getWorld().playSound(location, Sound.GHAST_FIREBALL, 0.1f, 1f);
        useEnergy(getEnergyUsageTicks());

        spawnFire(location);
        buff(location);
    }

    @Override
    public boolean cancel() {
        return !isToggled() || !hasEnergy(getEnergyUsageTicks()) || isInWater();
    }
    @Override
    public void cleanup() {
        if(!hasEnergy(getEnergyUsageTicks())) {
            forceToggle();
        }
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void damage(DamageApplyEvent event) {
        if(!isToggled() || event.getAttacker() != getPlayer()) return;
        if(event.getVictim().getFireTicks() <= 0) return;
        if(event.getCause() != Cause.MELEE) return;
        event.addSource(this);
        event.setModified(true);
        event.setDamage(event.getDamage() + 3);
        SoundPlayer.sendSound(getPlayer().getLocation(), "random.fizz", 0.8F, 63);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickUp(PlayerPickupItemEvent event) {
        if(event.getItem().getItemStack().getType() != Material.BLAZE_POWDER) return;
        if(event.getItem().getItemStack().getItemMeta() == null ||
                !event.getItem().getItemStack().getItemMeta().getDisplayName().contains(NAME)) return;
        Player victim = event.getPlayer();
        if(victim == getPlayer() || isAlly(victim)) return;
        StatusApplier.getOrNew(victim).applyStatus(Status.FIRE, 0.75F, 1);
        event.getItem().remove();
    }

}
