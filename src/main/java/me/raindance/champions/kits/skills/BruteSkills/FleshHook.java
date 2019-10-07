package me.raindance.champions.kits.skills.BruteSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.Main;
import me.raindance.champions.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.ChargeUp;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class FleshHook extends ChargeUp {
    private final int MAX_LEVEL = 5;
    private int damage;
    private float vect;
    private final SoundWrapper sound;
    private final WrapperPlayServerWorldParticles packet;

    public FleshHook(Player player, int level) {
        super(player, "Flesh Hook", level, SkillType.Brute, ItemType.SWORD, InvType.SWORD, 15 - level, (0.35F + 0.05F * level)/20F);
        int realRate = (int) (this.rate * 2000);
        this.damage = 5 + level;
        this.vect = 1.1F + 0.3F * level;
        packet = getPlayer() == null ? null : ParticleGenerator.createParticle(EnumWrappers.Particle.CRIT, 2);
        sound = new SoundWrapper("fire.ignite", 0.8F, 70);
        setDesc(Arrays.asList(
                "Hold block to charge Flesh Hook. ",
                "Release block to release it. ",
                "",
                "Charges %%rate%%% per second.",
                "",
                "If Flesh Hook hits a player, it ",
                "deals up to %%damage%% damage, and rips them ",
                "towards you with %%velocity%% velocity."
        ));
        addDescArg("damage", () ->  this.damage);
        addDescArg("velocity", () -> (double)((int)(this.vect * 100.0))/100.0);
        addDescArg("rate", () -> realRate);
    }

    @Override
    public void release() {
        Vector vector = getPlayer().getLocation().getDirection();
        double charge = getCharge();
        Vector itemVector = vector.clone().normalize().multiply(this.vect/1.25F);
        Location oldLocation = getPlayer().getLocation();
        Item itemItem = ItemManipulationManager.intercept(getPlayer(), Material.TRIPWIRE_HOOK, getPlayer().getEyeLocation(),itemVector.setY(itemVector.getY() + 0.2).multiply(0.5F + 0.5F * charge),
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
                    if(entity instanceof Player) DamageApplier.damage(entity, getPlayer(), charge * damage, this, false);
                }));
        itemItem.setPickupDelay(1000);
        ItemMeta meta = itemItem.getItemStack().getItemMeta();
        meta.setDisplayName(Long.toString(System.currentTimeMillis()));
        itemItem.getItemStack().setItemMeta(meta);
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> ParticleGenerator.generateEntity(itemItem, packet, sound), 1L);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
}

