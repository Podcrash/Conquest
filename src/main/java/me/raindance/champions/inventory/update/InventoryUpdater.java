package me.raindance.champions.inventory.update;

import java.util.ArrayList;
import java.util.List;

public class InventoryUpdater implements Runnable {
    private static List<IUpdateInv> inventories = new ArrayList<>();
    @Override
    public void run() {
        for(IUpdateInv inv : inventories) {
            inv.update();
        }
    }

    public static void add(IUpdateInv inv) {
        if(!inventories.contains(inv))
            inventories.add(inv);
    }
}
