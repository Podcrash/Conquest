package me.raindance.champions.kits.skills.thief;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.mc.disguise.Disguiser;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PrefixUtil;
import me.raindance.champions.Main;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.skilltypes.Continuous;
import com.podcrash.api.mc.mob.CustomSkeleton;
import com.podcrash.api.mc.util.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SkillMetadata(id = 704, skillType = SkillType.Thief, invType = InvType.SWORD)
public class Illusion extends Continuous implements ICooldown {
    private final int duration = 3;
    private long time;
    private boolean a = true;
    private Skeleton skeleton;

    @Override
    public float getCooldown() {
        return 16;
    }

    @Override
    public String getName() {
        return "Illusion";
    }

    public LivingEntity getSkeleton() {
        return skeleton;
    }
    @Override
    protected void doContinuousSkill() {
        if(a && !onCooldown()) {
            a = false;
            StatusApplier.getOrNew((getPlayer())).applyStatus(Status.CLOAK, duration, 1);
            time = System.currentTimeMillis();
            Skeleton skeleton = spawn();
            this.skeleton = skeleton;
            EntityEquipment entityEquipment = skeleton.getEquipment();
            entityEquipment.setArmorContents(getPlayer().getInventory().getArmorContents());
            entityEquipment.setItemInHand(getPlayer().getItemInHand());

            Disguiser.disguise(skeleton, getPlayer(), true, getPlayers());
            forceStop();

            startContinuousAction();
        } //else this.getEntity().sendMessage(getCooldownMessage());
    }

    private Skeleton spawn() {
        World world = getPlayer().getWorld();
        CustomSkeleton customSkeleton = new CustomSkeleton(world, getPlayer());
        Location l = getPlayer().getLocation();
        customSkeleton.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());// make sure u set the location with yaw and pitch or clients will crash
        customSkeleton.addWorld(world);
        return (Skeleton) customSkeleton.getBukkitEntity();
    }

    private void despawn(LivingEntity skeleton) {
        if(skeleton == null) return;
        //Bad way to prevent armor from showing after leaving illusion:
        //Teleport the skeleton far down (out of the way and doesn't load another chunk)
        skeleton.teleport(skeleton.getLocation().subtract(0, skeleton.getLocation().getY() + 10, 0));

        //Kill the skeleton down there, so armor will despawn from void
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            if(!skeleton.isDead()) skeleton.damage(skeleton.getMaxHealth());
                },4L);

    }

    @Override
    public void task() {
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking() || getPlayer().isDead() || (getSkeleton() == null || getSkeleton().isDead() ||
                System.currentTimeMillis() - time >= duration * 1000L);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (StatusApplier.getOrNew(getPlayer()).getEffects().contains(Status.CLOAK)) {
            StatusApplier.getOrNew((getPlayer())).removeCloak();
        }
        Location location = skeleton.getLocation();
        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(location.toVector(), EnumWrappers.Particle.SMOKE_LARGE, 9, 0.3F,0.4F,0.3F);
        List<Player> players = getPlayers();
        for(Player player : players) {
            if(player != getPlayer() && player.getLocation().distanceSquared(location) <= 9) {
                StatusApplier.getOrNew(player).applyStatus(Status.SLOW, duration, 1);
            }
        }
        PacketUtil.asyncSend(particles, players);
        for (int i=0 ; i<2 ; i++) location.getWorld().playSound(location, Sound.FIZZ, 2f, 0.4f);
        this.setLastUsed(System.currentTimeMillis());
        a = true;

        getGame().refreshTabColor(getPlayer(), getTeam().getChatColor().toString());

        //skeleton.getEquipment().setArmorContents(new ItemStack[]{null, null, null, null});

        despawn(getSkeleton());
    }

}
