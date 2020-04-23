package me.raindance.champions.ongoing;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.GameState;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ConquestTips implements TimeResource {

    private final ArrayList<String> tips;
    private final String url = "https://docs.google.com/document/d/10LcHuVMY-qiNGcbFWvK7pNWSHEiohzV4T-XBx93YqZ4/export?format=txt";

    private final ChatColor tipHeaderColor = ChatColor.YELLOW;
    private final String tipHeader = "Tip> ";

    private final Game game;

    public ConquestTips(Game game) {
        tips = new ArrayList<>();
        readTips(url);
        this.game = game;
    }

    private String getRandomTip() {
        if (tips.size() == 0) {
            return null;
        }
        Random rand = new Random();
        return tips.get(rand.nextInt(tips.size()));

    }

    private void readTips(String url) {
        try {
            URL conqTipsURL = new URL(url);
            Scanner reader = new Scanner(conqTipsURL.openStream());
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                tips.add(format(data));
            }
            reader.close();
        } catch (IOException err) {
            System.out.println("Could not read the Conquest Tips Document properly.");
            System.out.println(url);
            err.printStackTrace();
        }
    }

    private String format(String str) {
        str = str.trim(); //Clear leading/trailing whitespace
        str = str.replaceAll("\\P{Print}", "");
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }


    @Override
    public void task() {
        String tip = getRandomTip();
        if (tip == null || cancel()) {
            return;
        }
        Bukkit.broadcastMessage(tipHeaderColor + tipHeader + ChatColor.RESET + tip);
    }

    @Override
    public boolean cancel() {
        return (game.getGameState() != GameState.LOBBY || game != GameManager.getGame());
    }

    @Override
    public void cleanup() {
    }
}
