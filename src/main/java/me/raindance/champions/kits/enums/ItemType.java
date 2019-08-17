package me.raindance.champions.kits.enums;

public enum ItemType {
    SWORD("SWORD"), AXE("AXE"), BOW("BOW");
    private String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
