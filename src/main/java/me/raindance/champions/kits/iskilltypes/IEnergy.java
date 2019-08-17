package me.raindance.champions.kits.iskilltypes;

import me.raindance.champions.kits.ChampionsPlayer;

public interface IEnergy {
    /*
        Get energy usage in seconds
     */
    int getEnergyUsage();

    /*

     */
    default double getEnergyUsageTicks() {
        return getEnergyUsage() / 20D;
    }

    ChampionsPlayer getChampionsPlayer();
    default void useEnergy(double energy){
        getChampionsPlayer().getEnergyBar().setEnergy(getChampionsPlayer().getEnergyBar().getEnergy() - energy);
    }
}
