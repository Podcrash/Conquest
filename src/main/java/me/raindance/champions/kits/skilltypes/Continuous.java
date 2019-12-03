package me.raindance.champions.kits.skilltypes;

import me.raindance.champions.Main;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class Continuous extends Instant implements TimeResource {
    private boolean useOnce = true;

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    public Continuous() {
        super();
    }

    @Override
    protected void doSkill(PlayerInteractEvent event, Action action) {
        if(rightClickCheck(action)){
            doContinuousSkill();
            useOnce = false;
        }
    }

    protected abstract void doContinuousSkill();

    protected void start(){
        TimeHandler.repeatedTime(1,0, this);
    }

    protected void asyncStart(){
        TimeHandler.repeatedTimeAsync(1,0, this);
    }

    protected void forceStop(){
        TimeHandler.unregister(this);
    }

    @Override
    public void cleanup() {
        TimeHandler.unregister(this);
        Main.getInstance().getLogger().info("cleanup continuous called");
        useOnce = true;
    }
}
