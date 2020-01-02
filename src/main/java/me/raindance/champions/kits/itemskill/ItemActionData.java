package me.raindance.champions.kits.itemskill;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ItemActionData {
    private Material material;
    private Set<Action> actionSet;
    private IItem itemAction;

    public ItemActionData(Material material, Action[] actions, IItem itemAction) {
        this.material = material;
        this.actionSet = new HashSet<>(Arrays.asList(actions));
        this.itemAction = itemAction;
    }

    public boolean materialEquals(Material material) {
        return this.material == material;
    }
    public boolean actionContains(Action action) {
        return actionSet.contains(action);
    }

    public void doItemAction(Player player, Action action) {
        itemAction.useItem(player, action);
    }
}
