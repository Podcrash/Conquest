package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.damage.Cause;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.sound.SoundPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/*
    For arrow hits
 */
public abstract class BowShotSkill extends Instant {
    protected boolean isPrepared;
    private HashMap<Arrow, Float> arrowForceMap;
    public BowShotSkill(Player player, String name, int level, SkillType type, ItemType itype, InvType invType, float cooldown, boolean boosted) {
        super(player, name, level, type, itype, invType, cooldown);
        arrowForceMap = new HashMap<>();
    }
    /*
    Preparing an arrow (left click)
     */
    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if (!(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        if(!onCooldown()){
            isPrepared = true;// sound goes here
            SoundPlayer.sendSound(getPlayer().getLocation(), "mob.blaze.breathe", 0.75f, 200);
            this.getPlayer().sendMessage(String.format("%sSkill> %s%s %sprepared.", ChatColor.BLUE, ChatColor.GREEN, this.getName(), ChatColor.GRAY ));

            this.setLastUsed(System.currentTimeMillis());
            //arrowForceMap.keySet().removeIf(arr -> (arr.isDead() || !arr.isValid()));
        }
    }

    /*
    Shooting the arrow
     */
    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void shootBow(EntityShootBowEvent event){
        if(event.isCancelled() || !isPrepared) return;
        if(event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow){
            Player player = (Player) event.getEntity();
            if(player == getPlayer()){
                isPrepared = false;
                Arrow arrow = (Arrow) event.getProjectile();
                float currentForce = event.getForce();
                arrowForceMap.put(arrow, currentForce);
                shotArrow(arrow, arrowForceMap.get(arrow));
                /*
                getEntity().sendMessage(currentForce + "< -- currentforce");
                getEntity().sendMessage(arrowForceMap.get(arrow) + "<-- me.raindance.champions.map");
                getEntity().sendMessage(arrowForceMap.toString());
                */
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.BLUE);
                builder.append("Bow> ");
                builder.append(ChatColor.GRAY);
                builder.append(" You shot ");
                builder.append(ChatColor.BOLD);
                builder.append(ChatColor.YELLOW);
                builder.append(getName());
                builder.append(ChatColor.GRAY);
                builder.append(".");
            }
        }
    }

    protected abstract void shotArrow(Arrow arrow, float force);


    /*
                Shooting a player
             */
    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void arrowShotPlayer(DamageApplyEvent event){
        if(event.getCause() == Cause.PROJECTILE){
            LivingEntity livingEntity = event.getAttacker();
            Arrow proj = event.getArrow();
            if(livingEntity == getPlayer() && proj.getShooter() instanceof Player){
                if(arrowForceMap.containsKey(proj)){
                    if(event.getVictim() instanceof Player) {
                        shotPlayer(event, (Player) proj.getShooter(), (Player) event.getVictim(), proj, arrowForceMap.get(proj));
                    }
                    //proj.remove();
                }
            }
        }
    }
    protected abstract void shotPlayer(DamageApplyEvent event, Player shooter, Player victim, Arrow arrow, float force);

    /*
    Shooting the ground
     */
    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void arrowShotGround(ProjectileHitEvent event){
        if(event.getEntity() instanceof Arrow ){

            Arrow arr = (Arrow) event.getEntity();
            if(arr.getShooter() instanceof Player){
                Player shooter = (Player) arr.getShooter();
                if(shooter == getPlayer() && arrowForceMap.containsKey(arr)){
                    shotGround(shooter, event.getEntity().getLocation(), arr, arrowForceMap.get(arr));
                    //arrowForceMap.remove(arr);
                }

            }
        }
    }

    protected abstract void shotGround(Player shooter, Location location, Arrow arrow, float force);

}
