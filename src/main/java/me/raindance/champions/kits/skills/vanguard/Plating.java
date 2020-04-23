package me.raindance.champions.kits.skills.vanguard;

import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.game.GameResurrectEvent;
import com.podcrash.api.mc.events.game.GameStartEvent;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import me.raindance.champions.kits.annotation.SkillMetadata;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import me.raindance.champions.kits.iskilltypes.action.IConstruct;
import me.raindance.champions.kits.skilltypes.Passive;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

@SkillMetadata(id = 812, skillType = SkillType.Vanguard, invType = InvType.PRIMARY_PASSIVE)
public class Plating extends Passive implements TimeResource, IConstruct {

    private Long lastHit;
    private int waitTime = 8;

    @Override
    public String getName() {
        return "Plating";
    }

    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }

    @EventHandler
    public void onStart (GameStartEvent e) {
        resetTimer();
    }

    @EventHandler
    public void onRespawn(GameResurrectEvent e) {
        if(e.getWho().equals(getPlayer())) {
            resetTimer();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(DamageApplyEvent e) {

        CraftEntity craftEntity = (CraftEntity) getPlayer();
        EntityLiving player = (EntityLiving) craftEntity.getHandle();
        float extraHearts = player.getAbsorptionHearts();

        if(!e.isCancelled() && e.getVictim().equals(getPlayer()) && (extraHearts == 0)) {
            resetTimer();
            StatusApplier.getOrNew(getPlayer()).removeStatus(Status.ABSORPTION);
        }
    }

    /**
     * Updates the notRunning bool to reflect whether the skill is still waiting to active
     * @return Whether the skill activated on this check.
     */
    private boolean shouldApplyBuff() {
        //Bukkit.broadcastMessage("diff is " + (System.currentTimeMillis() - lastHit));
        return System.currentTimeMillis() - lastHit > (waitTime * 1000)
                && !getGame().isRespawning(getPlayer())
                && !DamageApplier.getInvincibleEntities().contains(getPlayer())
                && !StatusApplier.getOrNew(getPlayer()).has(Status.ABSORPTION);
    }

    private void resetTimer() {
        lastHit = System.currentTimeMillis();
    }

    @Override
    public void task() {
        if(shouldApplyBuff()) {
            StatusApplier.getOrNew(getPlayer()).applyStatus(Status.ABSORPTION, Integer.MAX_VALUE, 0, false, true);
            SoundPlayer.sendSound(getPlayer(), "mob.zombie.metal", 0.4f, 126);
            getPlayer().getWorld().playEffect(getPlayer().getLocation(), Effect.HEART, 1);
        }
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void afterConstruction() {
        TimeHandler.repeatedTime(5, 0, this);
        resetTimer();
    }
}
