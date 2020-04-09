package me.raindance.champions.kits.skills.warden;

import com.abstractpackets.packetwrapper.WrapperPlayClientSteerVehicle;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.StatusApplyEvent;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.world.BlockUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IInjector;
import me.raindance.champions.kits.skilltypes.Interaction;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.EntityEffect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

@SkillMetadata(id = 910, skillType = SkillType.Warden, invType = InvType.SWORD)
public class Hurl extends Interaction implements TimeResource, IInjector {
    private float multiplier = 1.25f;
    private boolean use = false;
    private long lastUsage;
    private Entity victim;
    private boolean stop = false;

    private void ensurePassenger(Entity clickedEntity) {
        getPlayer().setPassenger(clickedEntity);
        if(getPlayer().getPassenger() != clickedEntity) ensurePassenger(clickedEntity);
    }

    @EventHandler
    public void onStatusApply(StatusApplyEvent event) {
        if (event.getEntity() == getPlayer()) {
            Status status = event.getStatus();
            if (status.isNegative()) {
                stop = true;
            }
        }
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        return  stop || !getPlayer().isBlocking() || System.currentTimeMillis() - this.lastUsage >= 10000L ||
                ((CraftPlayer) getPlayer()).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() >= 1.3;
    }

    @Override
    public void cleanup() {
        if(victim.leaveVehicle()) {
            if(!BlockUtil.isSafe(victim.getLocation()))
                victim.teleport(getPlayer().getLocation());
            victim.playEffect(EntityEffect.HURT);

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
        if(victim != null && event.getVictim() == victim)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void suffo(EntityDamageEvent event) {
        if(event.getEntity() == victim && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION)
            event.setCancelled(true);
    }

    @Override
    public void doSkill(LivingEntity clickedEntity) {
        if(!onCooldown() && !use) {
            if (stop) {
                clickedEntity.sendMessage("you are debuffed! cannot grab");
                return;
            }
            victim = clickedEntity;
            victim.playEffect(EntityEffect.HURT);
            ensurePassenger(clickedEntity);
            this.lastUsage = System.currentTimeMillis();
            use = true;
            getPlayer().sendMessage("Dwarf Toss> You held " + clickedEntity.getName());
            run(1, 10);
        }
    }

    @Override
    public float getCooldown() {
        return 5;
    }

    @Override
    public String getName() {
        return "Hurl";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
