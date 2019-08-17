package me.raindance.champions.ongoing;


import java.util.ArrayList;
import java.util.List;

public final class TickManager {
    public static List<TickHelper> helpers = new ArrayList<>();


    public static int addHelper(TickHelper help) {
        helpers.add(help);
        return helpers.indexOf(help);
    }

    public static int getHelper(TickHelper helper) {
        return helpers.indexOf(helper);
    }

    public static void removeHelper(int id) {
        if (helpers.get(id) != null) {
            helpers.remove(id);
        }
    }

    public static List<TickHelper> getHelpers() {
        return helpers;
    }

    private TickManager() {
    }
}
