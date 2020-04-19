package me.raindance.champions.util;

import org.bukkit.ChatColor;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ConquestTips {

    private static ArrayList<String> tips;
    private static final String url = "https://docs.google.com/document/d/10LcHuVMY-qiNGcbFWvK7pNWSHEiohzV4T-XBx93YqZ4/export?format=txt";

    static {
        tips = new ArrayList<>();
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

    public static String getRandomTip() {
        if (tips.size() == 0) {
            return null;
        }
        Random rand = new Random();
        return tips.get(rand.nextInt(tips.size()));

    }

    private static String format(String str) {
        str = str.trim(); //Clear leading/trailing whitespace
        str = str.replaceAll("\\P{Print}", "");
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }




}
