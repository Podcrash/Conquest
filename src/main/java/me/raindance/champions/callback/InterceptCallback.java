package me.raindance.champions.callback;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

public interface InterceptCallback extends ICallback {
    void dorun(Item item, LivingEntity entity);
}
