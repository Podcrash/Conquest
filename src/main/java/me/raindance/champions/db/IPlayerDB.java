package me.raindance.champions.db;

import java.util.UUID;

public interface IPlayerDB {
    PlayerTable getPlayerTable();
    default long getID(UUID uuid) {
        return getPlayerTable().getID(uuid);
    }
}