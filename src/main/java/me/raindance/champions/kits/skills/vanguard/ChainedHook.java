package me.raindance.champions.kits.skills.vanguard;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.damage.DamageApplier;
import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.util.VectorUtil;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

@SkillMetadata(id = 801, skillType = SkillType.Vanguard, invType = InvType.SHOVEL)
public class ChainedHook extends Instant implements ICooldown {
    private float vect;

    @Override
    public String getName() {
        return "Chained Hook";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SHOVEL;
    }

    public ChainedHook() {
        super();
        this.vect = 1.1F + 0.3F * 5;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        release();

        getPlayer().sendMessage(getUsedMessage());
    }

    public void release() {
        Vector vector = getPlayer().getLocation().getDirection();
        double charge = 0.275D;
        Vector itemVector = vector.clone().normalize().multiply(this.vect/1.25F);
        Location oldLocation = getPlayer().getLocation();
        Item itemItem = ItemManipulationManager.intercept(Material.TRIPWIRE_HOOK, getPlayer().getEyeLocation(),itemVector.setY(itemVector.getY() + 0.2).multiply(0.5F + 0.5F * charge),
                ((item, entity) -> {
                    item.remove();
                    if(entity == null) return;
                    double amnt = 0.20F + 0.8F * charge;
                    Location away = entity.getLocation();
                    Vector newVect = VectorUtil.fromAtoB(away, oldLocation);
                    newVect.normalize().multiply(this.vect).multiply(amnt);
                    double addY = newVect.getY() + 0.4F  + 0.4F * amnt;
                    if(addY > 1.35F) addY = 1.35F;
                    entity.setVelocity(newVect.setY(addY));
                    SoundPlayer.sendSound(getPlayer(), "random.successful_hit", 0.75F, 50);
                    DamageApplier.damage(entity, getPlayer(), 6, this, false);
                }));
        itemItem.setPickupDelay(1000);
        ItemMeta meta = itemItem.getItemStack().getItemMeta();
        meta.setDisplayName(Long.toString(System.currentTimeMillis()));
        itemItem.getItemStack().setItemMeta(meta);

        SoundWrapper sound = new SoundWrapper("fire.ignite", 0.8F, 70);
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(EnumWrappers.Particle.CRIT, 2);

        ParticleGenerator.generateEntity(itemItem, packet, sound);
    }

    @Override
    public float getCooldown() {
        return 10;
    }

}

