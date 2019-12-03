package me.raindance.champions.kits.iskilltypes.action;

import com.podcrash.api.mc.time.resources.TimeResource;

public interface ICharge extends TimeResource {

    void addCharge();

    int getCurrentCharges();

    int getMaxCharges();
}
