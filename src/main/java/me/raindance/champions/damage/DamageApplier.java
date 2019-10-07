package me.raindance.champions.damage;

import com.abstractpackets.packetwrapper.WrapperPlayServerEntityVelocity;
import me.raindance.champions.kits.Skill;
import com.podcrash.api.mc.util.PacketUtil;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

public final class DamageApplier {
    public static void nativeApplyKnockback(LivingEntity epvictim, LivingEntity epdamager) {
        nativeApplyKnockback(epvictim, epdamager, new double[]{1D, 1D, 1D});
    }

    /**
     * SEt the knockback in relative to the position of the players
     * @param epvictim - victim
     * @param epdamager - attacker
     * @param velocityModifiers - in the form of x,y,z. These are multiplied after the initial calculations to determine how much kb one should take.
     */
    public static void nativeApplyKnockback(LivingEntity epvictim, LivingEntity epdamager, double[] velocityModifiers) {
        Entity livingVictim = ((CraftEntity) epvictim).getHandle();
        Entity livingDamager = ((CraftEntity) epdamager).getHandle();
        double d0 = livingVictim.motX;
        double d1 = livingVictim.motY;
        double d2 = livingVictim.motZ;
        a(livingVictim, livingDamager);

        double inRadian = Math.PI / 180.0D;
        double angle = livingDamager.yaw * inRadian;
        livingVictim.g((-FastMath.sin(angle) * SpigotConfig.knockbackExtraHorizontal),
                SpigotConfig.knockbackExtraVertical,
                (FastMath.cos(angle) * SpigotConfig.knockbackExtraHorizontal));

        livingVictim.motX *= velocityModifiers[0];
        livingVictim.motY *= velocityModifiers[1];
        livingVictim.motZ *= velocityModifiers[2];

        livingDamager.motX *= 0.6D;
        livingDamager.motZ *= 0.6D;

        sendVectorEvent(livingVictim, d0, d1, d2);
    }

    /**
     * Send the velocity event + packets to everybody
     * This uses the default bukkit way
     * @param livingVictim
     * @param d0
     * @param d1
     * @param d2
     */
    private static void sendVectorEvent(Entity livingVictim, double d0, double d1, double d2) {
        livingVictim.velocityChanged = true;

        if(livingVictim instanceof Player) {
            Player player = ((Player) livingVictim);
            Vector velocity = new Vector(d0, d1, d2);

            PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!velocity.equals(event.getVelocity())) {
                event.setVelocity(velocity);
            }
            if (!event.isCancelled()) {
                WrapperPlayServerEntityVelocity entityVelocity = new WrapperPlayServerEntityVelocity();
                entityVelocity.setEntityID(livingVictim.getId());
                entityVelocity.setVelocityX(livingVictim.motX);
                entityVelocity.setVelocityY(livingVictim.motY);
                entityVelocity.setVelocityZ(livingVictim.motZ);
                PacketUtil.syncSend(entityVelocity, player.getWorld().getPlayers());
                livingVictim.velocityChanged = false;
                livingVictim.motX = d0;
                livingVictim.motY = d1;
                livingVictim.motZ = d2;
            }
        }
    }

    //Base method, in a perfect world, entityLiving can be private
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, Arrow arrow, Skill skill, boolean applyKb, Cause cause) {
        if(victim.isDead() || attacker.isDead()) return; //prevent bs hits from dying
        //TODO: change to our own death system (ex: spectators)
        DamageQueue.getDamages().push(new Damage(victim, attacker, damage,
                attacker.getEquipment().getItemInHand(), cause, arrow, skill, applyKb));
    }
    //For good-ol melee
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, boolean applyKb) {
        damage(victim, attacker, damage, null, null, applyKb, Cause.MELEE);
    }
    //For arrows
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, Arrow arrow, boolean applyKb) {
        damage(victim, attacker, damage, arrow, null, applyKb, Cause.PROJECTILE);
    }
    //For skills
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage, Skill skill, boolean applyKb) {
        damage(victim, attacker, damage, null, skill, applyKb, Cause.SKILL);
    }
    //Just deal damage for no reason (no kb)
    public static void damage(LivingEntity victim, LivingEntity attacker, double damage) {
        damage(victim, attacker, damage, null, null, false, Cause.NULL);
    }

    /**
     * built in a method for entityliving (has something to do with velocity)
     * @param entityLiving1
     * @param entity
     */
    private static void a(Entity entityLiving1, Entity entity) {
        EntityLiving entityLiving;
        if(entityLiving1 instanceof EntityLiving) {
            entityLiving = (EntityLiving) entityLiving1;
        }else throw new IllegalArgumentException("entityLiving1 must be instance of " + EntityLiving.class);
        double d0 = entity.locX - entityLiving.locX;
        double d1 = entity.locZ - entityLiving.locZ;

        entityLiving.aw = (float)(FastMath.atan2(d1, d0) * 180.0D / 3.1415927410125732D - (double)entityLiving.yaw);
        //entityLiving.a(entity, 0F, d0, d1);
        double magnitude = FastMath.sqrt(d0 * d0 + d1 * d1);
        entityLiving.motX /= SpigotConfig.knockbackFriction;
        entityLiving.motY /= SpigotConfig.knockbackFriction;
        entityLiving.motZ /= SpigotConfig.knockbackFriction;

        entityLiving.motX -= d0 / magnitude * SpigotConfig.knockbackHorizontal;
        entityLiving.motY += SpigotConfig.knockbackVertical;
        entityLiving.motZ -= d1 / magnitude * SpigotConfig.knockbackHorizontal;
        if (entityLiving.motY > SpigotConfig.knockbackVerticalLimit) {
            entityLiving.motY = SpigotConfig.knockbackVerticalLimit;
        }
    }
}
