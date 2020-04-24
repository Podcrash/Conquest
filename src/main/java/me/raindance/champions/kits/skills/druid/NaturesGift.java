package me.raindance.champions.kits.skills.druid;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;

import java.util.Random;

@SkillMetadata(id = 204, skillType = SkillType.Druid, invType = InvType.AXE)
public class NaturesGift extends Instant implements ICooldown, IEnergy {
    @Override
    public float getCooldown() {
        return 15;
    }

    @Override
    public int getEnergyUsage() {
        return 60;
    }

    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if(!rightClickCheck(action) || onCooldown()) return;
        StatusApplier.getOrNew(getPlayer()).applyStatus(Status.STRENGTH, 7, 0, false);

        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.guardian.hit", 1F, 47);

        generateParticles(5, getPlayer());

        AbstractPacket healBurst = ParticleGenerator.createBlockEffect(getPlayer().getEyeLocation(), Material.EMERALD_BLOCK.getId());
        PacketUtil.asyncSend(healBurst, getPlayer().getWorld().getPlayers());

        getGame().consumeBukkitPlayer(this::buff);
        useEnergy(getEnergyUsage());
        setLastUsed(System.currentTimeMillis());
        getPlayer().sendMessage(getUsedMessage());

    }
    private void buff(Player victim) {
        if(victim == getPlayer() || !isAlly(victim)) return;
        if(victim.getLocation().distanceSquared(getPlayer().getLocation()) > 25) return;

        generateParticles(5, victim);

        //AbstractPacket healBurst = ParticleGenerator.createBlockEffect(getPlayer().getEyeLocation(), Material.EMERALD_BLOCK.getId());
        //PacketUtil.asyncSend(healBurst, getPlayer().getWorld().getPlayers());

        StatusApplier.getOrNew(victim).applyStatus(Status.STRENGTH, 7, 0, false);
    }
    @Override
    public String getName() {
        return "Nature's Gift";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.AXE;
    }

    private void generateParticles(int times, Player player){
        for(int i = 0; i < times; i++) {
            Random rand = new Random();
            WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(player.getEyeLocation().toVector(), EnumWrappers.Particle.VILLAGER_HAPPY,
                    3, rand.nextFloat(), 0.9f, rand.nextFloat());
            getPlayer().getWorld().getPlayers().forEach(p -> ParticleGenerator.generate(p, packet));
        }
    }
}
