package me.raindance.champions.kits.iskilltypes;

public interface ITogglePassive extends IDropPassive {
    boolean isToggled();
    void toggle();
}
