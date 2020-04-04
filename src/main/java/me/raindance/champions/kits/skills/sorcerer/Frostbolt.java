package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.ItemCollideEvent;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.Main;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

@SkillMetadata(id = 1011, skillType = SkillType.Sorcerer, invType = InvType.SWORD)
public class Frostbolt extends Instant implements IEnergy, ICooldown, IConstruct {
    private int currentItemID;

    private double damage = 4;
    private float cooldown = 1;
    private float slowDuration = 2;
    private int energyCost = 15;
    private String NAME;
    private WrapperPlayServerWorldParticles particles;

    private float speedMultiplier = 1.5f;
    private double arcAngle = 0.2;

    public Frostbolt() {

    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {

        // Check to make sure the skill is not on cool down, and the player has the energy required to activate.
        if(onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }

        // use frostbolt
        Location location = getPlayer().getEyeLocation();
        location.getWorld().playSound(location, Sound.ORB_PICKUP, 0.3f, 2f);
        Vector vector = location.getDirection().normalize().multiply(speedMultiplier);
        vector.setY(vector.getY() + arcAngle);

        Item spawnItem = ItemManipulationManager.regular(Material.SNOW_BALL, location, vector);
        this.currentItemID = spawnItem.getEntityId();
        org.bukkit.entity.Item iitem = ItemManipulationManager.intercept(spawnItem, 1.1,
                (item, entity, land) -> {
                    item.remove();
                    if (entity == null) return;
                    if(entity instanceof Player){
                        if(isAlly(entity)) {
                            location.getWorld().playSound(location, Sound.DIG_WOOL, 1f, 31.5f);
                        }
                        if(!isAlly((entity))) {
                            StatusApplier applier = StatusApplier.getOrNew(entity);
                            if(!applier.has(Status.FIRE))
                                applier.applyStatus(Status.SLOW, slowDuration, 1);
                            DamageApplier.damage(entity, getPlayer(), damage, this, false);
                        }
                    }else entity.damage(damage);
                    entity.getWorld().playEffect(land, Effect.STEP_SOUND, 20);
                });
        ItemMeta meta = iitem.getItemStack().getItemMeta();
        iitem.setCustomName("RITB");
        meta.setDisplayName(NAME + Long.toString(System.currentTimeMillis()));
        iitem.getItemStack().setItemMeta(meta);
        useEnergy(getEnergyUsage());
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> ParticleGenerator.generateEntity(iitem, particles, null), 1L);

        // Tell the game that we have finished using the skill (This is to trigger the cool down and create the cool down bar)
        setLastUsed(System.currentTimeMillis());
    }

    // Essentially, override the right click checker method to make it require left clicks instead (because that's how we want the skill to activate).
    @Override
    public boolean rightClickCheck(Action action) { return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK; }

    @Override
    public int getEnergyUsage() { return energyCost; }

    @Override
    public String getName() { return "Frostbolt"; }

    @Override
    public float getCooldown() { return cooldown; }

    @Override
    public ItemType getItemType() { return ItemType.SWORD; }

    @Override
    public void afterConstruction() {
        NAME = getName() + getPlayer().getName();
        particles = ParticleGenerator.createParticle(EnumWrappers.Particle.SNOW_SHOVEL, 1);
    }

    @EventHandler
    public void collideItem(ItemCollideEvent e) {
        if(e.isCancelled()) return;
        //identity check + owner of item check = cancel collision
        if(e.getCollisionVictim() == getPlayer() && e.getItem().getEntityId() == currentItemID)
            e.setCancelled(true);
    }

    @Override
    public boolean hasCooldown() {
        return false;
    }
}
