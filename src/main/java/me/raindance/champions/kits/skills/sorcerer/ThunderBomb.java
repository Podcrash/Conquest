package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.callback.helpers.TrapSetter;
import com.podcrash.api.mc.callback.sources.CollideBeforeHitGround;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.TrapPrimeEvent;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

@SkillMetadata(id = 1009, skillType = SkillType.Sorcerer, invType = InvType.SHOVEL)
public class ThunderBomb extends Instant implements IEnergy, ICooldown, IConstruct {
    private WrapperPlayServerWorldParticles particles;
    private String NAME;
    private int energy = 70;
    private float distance = 16;
    private int damage = 6;

    private int currentItemID;
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
    protected void doSkill(PlayerEvent event, Action action) {
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
        Item item = ItemManipulationManager.intercept(getPlayer(), Material.DIAMOND_BLOCK, location, vector, (item1, entity) -> {
            collide(item1);
        });
        item.setCustomName("RITB");
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(NAME + item.getEntityId());
        item.getItemStack().setItemMeta(meta);
        ParticleGenerator.generateEntity(item, particles, new SoundWrapper("random.fizz", 0.6F, 88));
        SoundPlayer.sendSound(item.getLocation(), "mob.silverfish.hit", 1F, 90);

        this.currentItemID = item.getEntityId();
        TrapSetter.spawnTrap(item, 500);
        getPlayer().sendMessage(getUsedMessage());
    }

    @EventHandler
    public void trapPrime(TrapPrimeEvent event) {
        Item item = event.getItem();
        if(item.getEntityId() != currentItemID) return;
        collide(item);
    }

    private void collide(Item item) {
        Location location = item.getLocation();
        for(Player player : getPlayers()) {
            if(player == getPlayer() && isAlly(player)) continue;
            Location playerLocation = player.getLocation();
            if(location.distanceSquared(playerLocation) > distance) continue;
            playerLocation.getWorld().strikeLightningEffect(playerLocation);
            StatusApplier.getOrNew(player).applyStatus(Status.SLOW, 4, 1);
            StatusApplier.getOrNew(player).applyStatus(Status.SHOCK, 4, 1);
            DamageApplier.damage(player, getPlayer(), damage, this, false);
        }

        TrapSetter.deleteTrap(item);
        item.remove();
    }

}
