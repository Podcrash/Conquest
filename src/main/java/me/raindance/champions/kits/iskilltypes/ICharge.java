package me.raindance.champions.kits.iskilltypes;

import me.raindance.champions.time.resources.TimeResource;

public interface ICharge extends TimeResource {

    void addCharge();

    int getCurrentCharges();

    int getMaxCharges();
}
