package me.raindance.champions.kits.iskilltypes.champion;

import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.enums.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ISkill extends Listener {
    int getID();
    String getName();
    ItemType getItemType();

    Player getPlayer();
    void setPlayer(Player player);
    default <T extends ChampionsPlayer> T getChampionsPlayer() {
        return (T) ChampionsPlayerManager.getInstance().getChampionsPlayer(getPlayer());
    }

}
