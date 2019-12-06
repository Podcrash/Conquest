package me.raindance.champions.kits.enums;

public enum ItemType {
    SWORD("SWORD"),
    AXE("AXE"),
    SHOVEL("SHOVEL"),
    BOW("BOW"),
    NULL(null);
    private String name;

    private final static ItemType[] details = ItemType.values();

    public static ItemType[] details() {
        return details;
    }

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
