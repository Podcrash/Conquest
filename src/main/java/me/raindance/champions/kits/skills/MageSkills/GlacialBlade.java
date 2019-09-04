package me.raindance.champions.kits.skills.MageSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.Main;
import me.raindance.champions.damage.DamageApplier;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

import static me.raindance.champions.world.BlockUtil.getPlayersInArea;

public class GlacialBlade extends Instant implements IEnergy {
    private final WrapperPlayServerWorldParticles particles;
    private final String NAME;
    private final int MAX_LEVEL = 3;
    private int energyUsage;
    public GlacialBlade(Player player, int level) {
        super(player, "Glacial Blade", level, SkillType.Mage, ItemType.SWORD, InvType.PASSIVEB, 1F - 0.1F * level);
        this.level = level;
        this.energyUsage = 11 - (2 * level);
        this.particles = (player == null) ? null : ParticleGenerator.createParticle(EnumWrappers.Particle.SNOW_SHOVEL, 1);
        this.NAME = (player == null) ? null : getName() + player.getName();
        setCanUseMessage(null);

        setDesc(Arrays.asList(
                "Swinging your sword releases a ",
                "shard of ice, dealing 3.5 damage ",
                "to anything it hits. ",
                "",
                "Will not work if enemies are close.",
                "",
                "Energy: %%energy%%"
        ));
        addDescArg("energy", () ->  energyUsage);
    }

    public boolean playersAreDistant() {
        List<Player> peopleInRange = getPlayersInArea(getPlayer().getLocation(), 4, getPlayers());
        for(Player p: peopleInRange) {
            if(p != getPlayer() && !isAlly(p)) return true;
        }
        return false;
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if(onCooldown() || playersAreDistant()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        this.setLastUsed(System.currentTimeMillis());
        Location location = getPlayer().getEyeLocation();
        location.getWorld().playSound(location, Sound.ORB_PICKUP, 1f, 2f);
        Vector vector = location.getDirection().normalize().multiply(0.7F + (0.1F * 5F));
        vector.setY(vector.getY() + 0.2);
        org.bukkit.entity.Item iitem = ItemManipulationManager.intercept(getPlayer(), Material.GHAST_TEAR, location, vector,
                (item, entity) -> {
                    if (entity == null) {// not hit

                    }else {
                        if(entity instanceof Player){
                            DamageApplier.damage(entity, getPlayer(), 3.5, this, false);
                        }else entity.damage(3.5);
                        entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, 20);
                    }
                    item.remove();
                });
        ItemMeta meta = iitem.getItemStack().getItemMeta();
        iitem.setCustomName("RITB");
        meta.setDisplayName(NAME + Long.toString(System.currentTimeMillis()));
        iitem.getItemStack().setItemMeta(meta);
        useEnergy(energyUsage);
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> ParticleGenerator.generateEntity(iitem, particles, null), 1L);
    }
}
