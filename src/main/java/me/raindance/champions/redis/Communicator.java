package me.raindance.champions.redis;

import com.comphenix.protocol.reflect.StructureModifier;
import me.raindance.champions.game.GameManager;
import org.bukkit.Bukkit;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Communicator {
    private static RedissonClient client;
    private static String code;
    private static RTopic controllerMessages;
    public static CompletableFuture<Void> setup(Executor executor) {
        System.out.println("[Redis] Starting!");
        Config config = new Config();
        String[] creds = getCredentials();
        System.out.println("[Redis] Credentials: " + creds[0] + " " + creds[1]);
        config.useSingleServer()
                .setAddress(creds[0])
                .setPassword(creds[1])
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2);

        client = Redisson.create(config);
        code = System.getProperty("lobby.code");
        System.out.println("[Redis] This lobby's code: " + code);
        controllerMessages = client.getTopic("controller-messages");

        ready();
        listeners();
        return CompletableFuture.runAsync(() -> {
            try {

            }catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);
    }

    private static void ready() {
        controllerMessages.publish(code + " READY");
        getMap().put("maxsize", Integer.toString(GameManager.getGame().getMaxPlayers()));

        System.out.println("[Redis] Ready! test: " + getMap().get("maxsize"));
    }
    private static void listeners() {

    }

    public static String getCode() {
        return code;
    }

    public static RMap<String, String> getMap() {
        return client.getMap(code);
    }

    public static void publish(String message) {
        controllerMessages.publish(message);
    }

    public static void shutdown() {
        client.shutdown();
    }
    private static String[] getCredentials() {
        final String HOST = System.getenv("REDIS_HOST");
        final String PASS = System.getenv("REDIS_PASS");
        if(HOST == null || PASS == null) {
            System.out.println(
                    "Failed to detect redis host and pass, stopping!\n" +
                    "Host: " + HOST + '\n' +
                    "Password: " + PASS);
            Bukkit.shutdown();
        }
        return new String[]{HOST, PASS};
    }
}
