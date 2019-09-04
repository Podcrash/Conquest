package me.raindance.champions.kits.skills.AssassinSkills;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.raindance.champions.disguise.Disguiser;
import me.raindance.champions.effect.particle.ParticleGenerator;
import me.raindance.champions.effect.status.Status;
import me.raindance.champions.effect.status.StatusApplier;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Continuous;
import me.raindance.champions.mob.CustomSkeleton;
import me.raindance.champions.util.PacketUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;

import java.util.Arrays;
import java.util.List;

public class Illusion extends Continuous {
    private final int MAX_LEVEL = 4;

    private Skeleton skeleton;

    private long time;
    private int duration;
    private boolean a = true;
    public Illusion(Player player, int level) {
        super(player, "Illusion", level, SkillType.Assassin, ItemType.SWORD, InvType.SWORD, 16 - level);
        this.duration = 2 + this.level;

        setDesc(Arrays.asList(
                "Hold block to turn invisible and create an ",
                "Illusion of yourself that runs towards",
                "where you are looking.",
                "",
                "You reappear if you release block or",
                "if the Illusion dies.",
                "",
                "The Illusion automatically dies after giving ",
                "%%duration%% seconds giving Slowness 2 for %%duration%% seconds",
                "to nearby enemies when doing so."
        ));
        addDescArg("duration", () -> duration);
    }

    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    @Override
    protected void doContinuousSkill() {
        if(a && !onCooldown()) {
            a = false;
            this.getPlayer().sendMessage(getUsedMessage());
            StatusApplier.getOrNew((getPlayer())).applyStatus(Status.CLOAK, duration, 1);
            time = System.currentTimeMillis();
            skeleton = spawn();
            EntityEquipment entityEquipment = skeleton.getEquipment();
            entityEquipment.setArmorContents(getPlayer().getInventory().getArmorContents());
            entityEquipment.setItemInHand(getPlayer().getItemInHand());

            Disguiser.disguise(skeleton, getPlayer(), true, getPlayers());
            forceStop();

            start();
        } //else this.getEntity().sendMessage(getCooldownMessage());
    }

    private Skeleton spawn() {
        World world = getPlayer().getWorld();
        CustomSkeleton customSkeleton = new CustomSkeleton(world, getPlayer());
        Location l = getPlayer().getLocation();
        customSkeleton.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());// make sure u set the location with yaw and pitch or clients will crash
        ((CraftWorld) world).getHandle().addEntity(customSkeleton, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Skeleton) customSkeleton.getBukkitEntity();
    }

    private void despawn(Skeleton skeleton) {
        skeleton.teleport(skeleton.getLocation().subtract(0, skeleton.getLocation().getY(), 0));
        if(!skeleton.isDead()) skeleton.damage(skeleton.getMaxHealth());
    }

    @Override
    public void task() {

    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking() || (skeleton.isDead() ||
                System.currentTimeMillis() - time >= duration * 1000L);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        StatusApplier.getOrNew((getPlayer())).removeCloak();
        Location location = skeleton.getLocation().clone();
        despawn(skeleton);

        WrapperPlayServerWorldParticles particles = ParticleGenerator.createParticle(location.toVector(), EnumWrappers.Particle.SMOKE_LARGE, 9, 0.3F,0.4F,0.3F);
        List<Player> players = getPlayers();
        for(Player player : players) {
            if(player != getPlayer() && player.getLocation().distanceSquared(location) <= 9) {
                StatusApplier.getOrNew(player).applyStatus(Status.SLOW, duration, 1);
            }
        }
        PacketUtil.syncSend(particles, players);
        for (int i=0 ; i<2 ; i++) location.getWorld().playSound(location, Sound.FIZZ, 2f, 0.4f);
        this.setLastUsed(System.currentTimeMillis());
        a = true;
    }
}
