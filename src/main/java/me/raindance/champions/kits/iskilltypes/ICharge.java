package me.raindance.champions.kits.iskilltypes;

import com.podcrash.api.mc.time.resources.TimeResource;

public interface ICharge extends TimeResource {

    void addCharge();

    int getCurrentCharges();

    int getMaxCharges();
}
