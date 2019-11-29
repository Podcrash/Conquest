package me.raindance.champions.kits.skills.BruteSkills;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.mc.callback.sources.CollideBeforeHitGround;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.skilltypes.Instant;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.PacketUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Takedown extends Instant {
    private final float hitbox = 0.55f;
    private CollideBeforeHitGround hitGround;
    private int damage;
    private int selfDamage;
    private int effect;

    public Takedown(Player player, int level) {
        super(player, "Takedown", level, SkillType.Brute, ItemType.AXE, InvType.AXE, 17 - level);
        this.damage = 4 + level;
        this.selfDamage = 1 + level;
        this.effect = 2 + level;
        WrapperPlayServerWorldParticles packet = ParticleGenerator.createParticle(EnumWrappers.Particle.CRIT, 2);
        this.hitGround = new CollideBeforeHitGround(player)
                .changeEvaluation(() -> (player.getNearbyEntities(hitbox, hitbox, hitbox).size() > 0) || EntityUtil.onGround(player))
                .then(() -> {
                    SkillUseEvent event = new SkillUseEvent(this);
                    Bukkit.getPluginManager().callEvent(event);
                    if(event.isCancelled()) return;
                    List<Entity> entities = getPlayer().getNearbyEntities(hitbox, hitbox, hitbox);
                    if (entities.size() == 0) return;
                    getPlayer().setVelocity(new Vector(0, 0, 0));
                    for (Entity entity : entities) {
                        if (entity instanceof Player && entity != getPlayer()) {
                            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ZOMBIE_WOOD, 2f, 0.2f);
                            DamageApplier.damage((LivingEntity) entity, getPlayer(), damage, this, false);
                            DamageApplier.damage(getPlayer(), (Player) entity, selfDamage, this, false);
                            StatusApplier.getOrNew((Player) entity).applyStatus(Status.SLOW, effect, 3);
                            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.SLOW, effect, 3);

                            StatusApplier.getOrNew((Player) entity).applyStatus(Status.GROUND, effect, 3);
                            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.GROUND, effect, 3);

                            getPlayer().sendMessage(String.format("%s%s> You used %sTakedown%s on %s", ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GREEN, ChatColor.GRAY, entity.getName()));

                            break; //only attack one player
                        }
                    }
                }).doWhile(() -> {
                    packet.setLocation(player.getLocation());
                    PacketUtil.syncSend(packet, getPlayers());
                });

        setDesc(Arrays.asList(
                "Hurl yourself towards an opponent. ",
                "If you collide with them, you deal ",
                "%%damage%% damage and take %%selfdamage%% damage. ",
                "You both receive Slow 4 for %%duration%% seconds. "
        ));
        addDescArg("damage", () ->  damage);
        addDescArg("selfdamage", () -> selfDamage);
        addDescArg("duration", () -> effect);
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (!rightClickCheck(action) || EntityUtil.onGround(getPlayer())) {
            if(!onCooldown()) {
                getPlayer().sendMessage(String.format("%s%s> %sYou cannot use %sTakedown%s while grounded.",
                        ChatColor.BLUE, getChampionsPlayer().getName() , ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY));
            }
            return;
        }
        if (!onCooldown()) {
            this.setLastUsed(System.currentTimeMillis());
            Vector vector = getPlayer().getLocation().getDirection().normalize().multiply(1.1d).setY(0f);
            getPlayer().setVelocity(vector);
            getPlayer().setFallDistance(0);
            hitGround.run();
        }
    }


    @Override
    public int getMaxLevel() {
        return 5;
    }
}
