package me.raindance.champions.kits.skills.BruteSkills;

import com.comphenix.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.IInjector;
import me.raindance.champions.kits.skilltypes.Interaction;
import me.raindance.champions.time.resources.TimeResource;
import me.raindance.champions.world.BlockUtil;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;

public class DwarfToss extends Interaction implements TimeResource, IInjector {
    private float multiplier;
    private boolean use = false;
    private long lastUsage;
    private Entity victim;
    private HashMap<String, Long> delays = new HashMap<>();
    public DwarfToss(Player player, int level) {
        super(player, "Dwarf Toss", level, SkillType.Brute, ItemType.SWORD, InvType.SWORD, 20 - level);
        this.multiplier = 0.8F + 0.15F * getLevel();
        setDesc(Arrays.asList(
                "Hold block to pick up target player.",
                "Release block to throw target player.",
                "",
                "Players you are holding cannot harm ",
                "you, or be harmed by others."
        ));
    }

    @Override
    public void doSkill(Entity clickedEntity) {
        if(!onCooldown() && !use) {
            victim = clickedEntity;
            victim.playEffect(EntityEffect.HURT);
            ensurePassenger(clickedEntity);
            this.lastUsage = System.currentTimeMillis();
            use = true;
            getPlayer().sendMessage("Dwarf Toss> You held " + clickedEntity.getName());
            run(1, 10);
        }
    }

    private void ensurePassenger(Entity clickedEntity) {
        getPlayer().setPassenger(clickedEntity);
        if(getPlayer().getPassenger() != clickedEntity) ensurePassenger(clickedEntity);
    }
    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        return  !getPlayer().isBlocking() || System.currentTimeMillis() - this.lastUsage >= 10000L ||
                ((CraftPlayer) getPlayer()).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() >= 1.3;
    }

    @Override
    public void cleanup() {
        if(victim.leaveVehicle()) {
            if(!BlockUtil.isSafe(victim.getLocation()))
                victim.teleport(getPlayer().getLocation());
            victim.playEffect(EntityEffect.HURT);
            delays.put(victim.getName(), System.currentTimeMillis());

            Vector vector = getPlayer().getLocation().getDirection();
            vector.normalize().multiply(multiplier);
            victim.setVelocity(vector);
        }

        victim = null;
        use = false;
        setLastUsed(System.currentTimeMillis());

    }

    @Override
    public PacketType[] getTypes() {
        return new PacketType[]{PacketType.Play.Client.STEER_VEHICLE};
    }

    @Override
    public void send(PacketEvent var1) {

    }

    @Override
    public void recieve(PacketEvent event) {
        WrapperPlayClientSteerVehicle packet = new WrapperPlayClientSteerVehicle(event.getPacket());
        if(packet.isUnmount() && event.getPlayer() == victim)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void hit(DamageApplyEvent event) {
        if(victim != null && (event.getVictim() == victim || event.getAttacker() == victim))
            event.setCancelled(true);
        if (delays.containsKey(event.getVictim().getName())) {
            String name = event.getVictim().getName();
            long time = delays.get(name);
            if (System.currentTimeMillis() - time >= 1200L) delays.remove(name);
            else event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void suffo(EntityDamageEvent event) {
        if(event.getEntity() == victim && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)
            event.setCancelled(true);
    }
}
