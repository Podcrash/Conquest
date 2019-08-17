package me.raindance.champions.kits.skills.MageSkills;

import me.raindance.champions.Main;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.world.BlockUtil;
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

public class Immolate extends TogglePassive implements IEnergy, TimeResource {
    private final int MAX_LEVEL = 1;
    private final Runnable buff;
    private final Vector up = new Vector(0, 0.34, 0);
    private final String NAME;
    private final Random random = new Random();

    private int energy;
    public Immolate(Player player, int level) {
        super(player, "Immolate", level, "fire", SkillType.Mage, InvType.PASSIVEA);
        this.energy = 7;
        buff = (player == null) ? null : () -> {
            boolean cancel = false;
            if(hasEnergy(getEnergyUsageTicks())) {
                useEnergy(getEnergyUsageTicks());
                getChampionsPlayer().getEnergyBar().toggleRegen(false);
                List<Player> players = BlockUtil.getPlayersInArea(getPlayer().getLocation(), 6, getPlayers());
                for (Player p : players) {
                    if (p == getPlayer() || isAlly(p)) {
                        double energyReq = (players.size() - 1) / 20D;
                        if(!hasEnergy(energyReq)) cancel = true;
                        if(energyReq <= 0) useEnergy(energyReq);
                        StatusApplier.getOrNew(p).applyStatus(Status.SPEED, 0.5F, 0, false);
                        StatusApplier.getOrNew(p).applyStatus(Status.FIRE_RESISTANCE, 0.5F, 0, false);
                        StatusApplier.getOrNew(p).applyStatus(Status.STRENGTH, 0.5F, 0, false);
                    }
                }
            }else {
                cancel = true;
            }
            if(cancel) forceToggle();
        };
        NAME = (player == null) ? null : player.getName() + "INFERNO";
        setDesc(Arrays.asList(
                "Drop Axe/Sword to Toggle. ",
                "",
                "Ignite yourself in flaming fury. ",
                "You receive Strength 1, Speed 1, ",
                "and Fire Resistance. ",
                "",
                "You leave a trail of fire, which ",
                "ignites players for %%duration%% seconds.",
                "",
                "Energy: %%energy%% per Second"
        ));
        addDescArg("duration", () ->  0.5);
        addDescArg("energy", () -> 14);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
    @Override
    public int getSkillTokenWeight() {
        return 2;
    }
    @Override
    public int getEnergyUsage() {
        return energy;
    }

    @Override
    public double getEnergyUsageTicks() {
        return 14D/20D;
    }

    @Override
    public void toggle() {
        if(isToggled()) {
            if(hasEnergy(energy)) {
                useEnergy(energy);
                run(1, 0);
            }
        } else {
            getChampionsPlayer().getEnergyBar().toggleRegen(true);
            unregister();
        }
    }

    private byte a = 0;
    @Override
    public void task() {
        if(isInWater()) forceToggle();
        buff.run();
        if(getPlayer().getFireTicks() > 0) StatusApplier.getOrNew(getPlayer()).removeStatus(Status.FIRE);
        a++;
        Location location = getPlayer().getLocation();
        location.getWorld().playSound(location, Sound.GHAST_FIREBALL, 0.1f, 1f);
        if(a % 2 == 0) {
            a = 0;

            float randomizer = 0.1F * random.nextFloat();
            up.setX(randomizer);
            up.setZ(randomizer);
            Item item = ItemManipulationManager.regular(Material.BLAZE_POWDER, getPlayer().getLocation(), up);
            item.setCustomName("RITB");
            ItemMeta meta = item.getItemStack().getItemMeta();
            meta.setDisplayName(NAME + item.getEntityId());
            item.getItemStack().setItemMeta(meta);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, item::remove, 10);
        }
    }

    @Override
    public boolean cancel() {
        return !isToggled();
    }
    @Override
    public void cleanup() {
        toggle();
    }

    @EventHandler(priority =  EventPriority.HIGH)
    public void damage(DamageApplyEvent event) {
        if(isToggled() && event.getAttacker() == getPlayer()) {
            event.addSkillCause(this);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickUp(PlayerPickupItemEvent event) {
        if(event.getItem().getItemStack().getItemMeta().getDisplayName().contains(NAME)) {
            Player victim = event.getPlayer();
            if(victim == getPlayer() || isAlly(victim)) return;
            StatusApplier.getOrNew(victim).applyStatus(Status.FIRE, 0.5F, 1);
            event.getItem().remove();
        }
    }

}
