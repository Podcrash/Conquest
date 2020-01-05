package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.sound.SoundWrapper;
import net.jafama.FastMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

@SkillMetadata(id = 1009, skillType = SkillType.Sorcerer, invType = InvType.SHOVEL)
public class ThunderBomb extends Instant implements IEnergy, ICooldown, IConstruct {
    private WrapperPlayServerWorldParticles particles;
    private String NAME;
    private int energy = 80;
    private float distance = 4;
    private int damage = 6;

    @Override
    public void afterConstruction() {
        this.NAME = getPlayer().getName()  + getName();
        this.particles = ParticleGenerator.createParticle(null, EnumWrappers.Particle.SNOW_SHOVEL, 1, 0, 0, 0);

    }

    @Override
    public float getCooldown() {
        return 6;
    }

    @Override
    public String getName() {
        return "Thunder Bomb";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    @Override
    public int getEnergyUsage() {
        return energy;
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }
        this.setLastUsed(System.currentTimeMillis());
        Location location = getPlayer().getEyeLocation();
        Vector vector = location.getDirection();
        vector.normalize().multiply(1.15D);
        useEnergy(energy);
        Item item = ItemManipulationManager.interceptWithCooldown(getPlayer(), Material.DIAMOND_BLOCK, location, vector, 3.0F, ((item1, entity) -> {
            Location location1 = item1.getLocation();
            for(Player player : getPlayers()) {
                if(player == getPlayer() && isAlly(player)) continue;
                Location playerLocation = player.getLocation();
                if(location1.distanceSquared(playerLocation) > distance) continue;
                playerLocation.getWorld().strikeLightningEffect(playerLocation);
                StatusApplier.getOrNew(player).applyStatus(Status.SLOW, 4, 1);
                StatusApplier.getOrNew(player).applyStatus(Status.SHOCK, 4, 1);
                DamageApplier.damage(player, getPlayer(), damage, this, false);
            }
            item1.remove();
        }));
        item.setCustomName("RITB");
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(NAME + item.getEntityId());
        item.getItemStack().setItemMeta(meta);
        ParticleGenerator.generateEntity(item, particles, new SoundWrapper("random.fizz", 0.6F, 88));
        SoundPlayer.sendSound(item.getLocation(), "mob.silverfish.hit", 1F, 90);
    }

}
