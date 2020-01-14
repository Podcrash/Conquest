package me.raindance.champions.kits.itemskill;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ItemActionData {
    private final Material material;
    private final Set<Action> actionSet;
    private final IItem itemAction;
    private final int matId;

    public ItemActionData(Material material, Action[] actions, IItem itemAction, int matId) {
        this.material = material;
        this.actionSet = new HashSet<>(Arrays.asList(actions));
        this.itemAction = itemAction;
        this.matId = matId;
    }

    public boolean materialEquals(Material material, MaterialData matData) {
        return this.material == material && (this.matId == -1) || this.matId == matData.getData();
    }
    public boolean actionContains(Action action) {
        return actionSet.contains(action);
    }

    public void doItemAction(Player player, Action action) {
        itemAction.useItem(player, action);
    }
}
