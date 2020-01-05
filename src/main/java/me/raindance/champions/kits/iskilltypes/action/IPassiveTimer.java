package me.raindance.champions.kits.iskilltypes.action;

public interface IPassiveTimer {
    void start();
    default void stop() {

    }
}
