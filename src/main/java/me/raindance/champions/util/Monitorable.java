package me.raindance.champions.util;

import java.util.ArrayList;
import java.util.List;

public interface Monitorable {
    List<Monitor> monitors = new ArrayList<>();
    void register(Monitor monitor);
    void unregister(Monitor monitor);
}
