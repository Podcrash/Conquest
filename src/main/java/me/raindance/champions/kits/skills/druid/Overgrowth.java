package me.raindance.champions.kits.skills.druid;

import com.packetwrapper.abstractpackets.AbstractPacket;
import com.packetwrapper.abstractpackets.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.effect.particle.ParticleGenerator;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.world.BlockUtil;
import me.raindance.champions.annotation.kits.SkillMetadata;
import com.podcrash.api.kits.enums.InvType;
import com.podcrash.api.kits.enums.ItemType;
import me.raindance.champions.kits.SkillType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.iskilltypes.action.IEnergy;
import com.podcrash.api.kits.skilltypes.Instant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

@SkillMetadata(id = 206, skillType = SkillType.Druid, invType = InvType.SWORD)
public class Overgrowth extends Instant implements ICooldown, IEnergy {
    private int energyUsage = 90;
    private int radius = 5;
    private float duration = 5;

    public Overgrowth() { }

    @Override
    public void doSkill(PlayerEvent event, Action action) {
        if(onCooldown()) return;

        // Create a list of all the players near us, and then iterate through it to find the allies
        List<Player> near = BlockUtil.getPlayersInArea(getPlayer().getLocation(), radius, getPlayers());
        List<Player> nearAllies = new ArrayList<>();

        for (Player player : near) {
            if (isAlly(player)) nearAllies.add(player);
        }

        // For every ally within the radius, apply a buff. If there are only 2 allies, the buff is stronger.
        // Send them all particles and sounds too cus its nice.
        for (Player player : nearAllies) {
            if(nearAllies.size() > 2)
                StatusApplier.getOrNew(player).applyStatus(Status.ABSORPTION, duration, 0);
            else
                StatusApplier.getOrNew(player).applyStatus(Status.ABSORPTION, duration, 1);

            WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(player.getLocation().toVector(), EnumWrappers.Particle.HEART,
                    3, 0, 0.9f, 0);
            getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
            AbstractPacket leafBreak = ParticleGenerator.createBlockEffect(player.getLocation(), Material.LEAVES.getId());
            PacketUtil.asyncSend(leafBreak, getPlayer().getWorld().getPlayers());

            SoundPlayer.sendSound(getPlayer().getLocation(), "mob.enderdragon.wings", 0.8F, 1);
        }
        setLastUsed(System.currentTimeMillis());
        useEnergy();
    }

    @Override
    public float getCooldown() {
        return 10;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public String getName() {
        return "Overgrowth";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }
}
