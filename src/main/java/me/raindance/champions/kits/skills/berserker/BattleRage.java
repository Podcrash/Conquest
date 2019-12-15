package me.raindance.champions.kits.skills.berserker;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.EnergyBar;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.ICooldown;
import me.raindance.champions.kits.iskilltypes.action.IEnergy;
import me.raindance.champions.kits.skilltypes.Drop;
import me.raindance.champions.kits.skilltypes.Instant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@SkillMetadata(skillType = SkillType.Berserker, invType = InvType.AXE)
public class BattleRage extends Drop implements ICooldown, IEnergy {
    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public float getCooldown() {
        return 19;
    }

    @Override
    public String getName() {
        return "Battle Rage";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @Override
    public void drop(PlayerDropItemEvent e) {
        if(e.getPlayer() != getPlayer() || onCooldown()) return;
        setLastUsed(System.currentTimeMillis());
        EnergyBar energyBar = getChampionsPlayer().getEnergyBar();
        getChampionsPlayer().heal(energyBar.getEnergy());

        Location loc = getPlayer().getLocation();
        SoundPlayer.sendSound(loc, "mob.enderdragon.growl", 0.9F, 80);
        ParticleGenerator.createBlockEffect(loc, Material.REDSTONE_BLOCK.getId());
    }
}
