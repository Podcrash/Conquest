package me.raindance.champions.kits.enums;

public enum InvType {
    SWORD("Sword"),
    AXE("Axe"),
    BOW("Bow"),
    PASSIVEA("Passive A"),
    PASSIVEB("Passive B"),
    PASSIVEC("Passive C");

    private String name;
    InvType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
