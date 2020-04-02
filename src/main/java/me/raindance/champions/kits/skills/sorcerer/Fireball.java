package me.raindance.champions.kits.skills.sorcerer;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.util.PacketUtil;
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
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

@SkillMetadata(id = 1010, skillType = SkillType.Sorcerer, invType = InvType.SWORD)
public class Fireball extends Instant implements IEnergy, ICooldown, IConstruct {

    private double damage = 4;
    private float cooldown = 1;
    private float burnDuration = 3;
    private int energyCost = 15;
    private String NAME;
    private WrapperPlayServerWorldParticles particles;

    private float speedMultiplier = 1.5f;
    private double arcAngle = 0.2;

    public Fireball() {}

    @Override
    protected void doSkill(PlayerEvent event, Action action) {

        // Check to make sure the skill is not on cool down, and the player has the energy required to activate.
        if(onCooldown()) return;
        if(!hasEnergy()) {
            getPlayer().sendMessage(getNoEnergyMessage());
            return;
        }

        // use fireball
        Location location = getPlayer().getEyeLocation();
        location.getWorld().playSound(location, Sound.GHAST_FIREBALL, 0.4f, 100.8f);
        Vector vector = location.getDirection().normalize().multiply(speedMultiplier);
        vector.setY(vector.getY() + arcAngle);
        org.bukkit.entity.Item iitem = ItemManipulationManager.intercept(Material.MAGMA_CREAM, location, vector,
                (item, entity) -> {
                    if (entity == null) {// not hit

                    }else {
                        if(entity instanceof Player){
                            if(isAlly(entity)) {
                                location.getWorld().playSound(location, Sound.DIG_WOOL, 1f, 31.5f);
                                item.remove();
                                return;
                            }
                            if(!isAlly((entity)) && !BlockUtil.isInWater(entity)) {
                                StatusApplier applier = StatusApplier.getOrNew(entity);
                                if(!applier.has(Status.FIRE))
                                    applier.applyStatus(Status.FIRE, burnDuration, 1);
                                DamageApplier.damage(entity, getPlayer(), damage, this, false);
                            }
                        }else entity.damage(damage);
                        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(item.getLocation().toVector(), EnumWrappers.Particle.EXPLOSION_LARGE, new int[]{0,0,0}, 1, 0,0,0);
                        PacketUtil.asyncSend(packet, getPlayers());
                        location.getWorld().playSound(location, Sound.DIG_WOOL, 1f, 31.5f);
                        location.getWorld().playSound(location, Sound.EXPLODE, 0.3f, 100f);
                    }
                    item.remove();
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
    public String getName() { return "Fireball"; }

    @Override
    public ItemType getItemType() { return ItemType.SWORD; }

    @Override
    public float getCooldown() { return cooldown; }

    @Override
    public void afterConstruction() {
        NAME = getName() + getPlayer().getName();
        particles = ParticleGenerator.createParticle(EnumWrappers.Particle.SMOKE_NORMAL, 1);
    }

    @Override
    public boolean hasCooldown() {
        return false;
    }
}
