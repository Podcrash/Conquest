package me.raindance.champions.kits.iskilltypes.champion;

import com.podcrash.api.db.DataTableType;
import com.podcrash.api.db.DescriptorTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.redis.Communicator;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.annotation.*;
import me.raindance.champions.kits.enums.InvType;
import me.raindance.champions.kits.enums.ItemType;
import me.raindance.champions.kits.enums.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ISkill extends Listener {
    int getID();
    String getName();
    ItemType getItemType();

    //TODO: REMOVE THIS
    default int getLevel() {
        return 1;
    }

    Player getPlayer();
    void setPlayer(Player player);
    default <T extends ChampionsPlayer> T getChampionsPlayer() {
        return (T) ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer());
    }

}
