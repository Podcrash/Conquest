package me.raindance.champions;

import de.caluga.morphium.Morphium;
import de.caluga.morphium.MorphiumConfig;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static Map<String, Database> databases = new HashMap<>();
    private Morphium morphium;
    private MorphiumConfig config;

    public static Database get(String document) {
        return databases.get(document);
    }
    public Database(String document) {
        config = new MorphiumConfig();
        config.setDatabase(document);
        config.addHostToSeed(System.getenv("CHAMPIONS_MONGO_DB_HOST"), Integer.parseInt(System.getenv("CHAMPIONS_MONGO_DB_PORT")));
        this.morphium = new Morphium(config);
        databases.put(document, this);
    }

    public void read() {
    }

    public Morphium getMorphium() {
        return morphium;
    }
}
