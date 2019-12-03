package me.raindance.champions.kits.enums;

public enum ItemType {
    SWORD("SWORD"),
    AXE("AXE"),
    SHOVEL("SHOVEL"),
    BOW("BOW"),
    NULL(null);
    private String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
