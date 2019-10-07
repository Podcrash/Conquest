package me.raindance.champions.kits.skilltypes;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.mc.util.TitleSender;
import me.raindance.champions.Main;
import me.raindance.champions.events.skill.SkillUseEvent;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.util.SkillTitleSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

/*
    This is with swords most likely
 */
public abstract class ChargeUp extends Skill implements TimeResource {
    protected boolean isUsing = false;
    private double power = 0;
    protected double rate = 0;
    public ChargeUp(Player player, String name, int level, SkillType type, ItemType itype, InvType invType, float cooldown, float rate) {
        super(player, name, level, type, itype, invType, cooldown);
        this.rate = rate;
    }
    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void block(PlayerInteractEvent e){
        if(e.getPlayer() == this.getPlayer()){
            if(rightClickCheck(e.getAction()) && isHolding()){
                if(!onCooldown()) {
                    SkillUseEvent useEvent = new SkillUseEvent(this);
                    Bukkit.getPluginManager().callEvent(useEvent);
                    if(useEvent.isCancelled()) return;
                    if(!isInWater()) TimeHandler.repeatedTime(1, 0, this);
                    else getPlayer().sendMessage(getWaterMessage());
                }
            }
        }
    }

    public abstract void release();
    @Override
    public void task() {
        charge();
        isUsing = true;
        WrappedChatComponent progress = SkillTitleSender.chargeUpProgressBar(this, this.getCharge());
        if(getCharge() < 1f) SoundPlayer.sendSound(this.getPlayer(), "note.harp", 0.75f, (int)(130 * getCharge()) );
        TitleSender.sendTitle(this.getPlayer(), progress);
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking();
    }

    @Override
    public void cleanup() {
        TimeHandler.unregister(this);
        if(power >= 1D) power = 1D;
        Bukkit.getScheduler().runTask(Main.instance, () -> {
            release();
            resetCharge();
        });
        setLastUsed(System.currentTimeMillis());
        isUsing = false;
    }

    protected void charge(){
        power += rate;
    }
    protected void charge(double boost){
        power += boost;
    }

    protected double getCharge() {
        return power;
    }

    protected void resetCharge(){
        power = 0;
    }
}
