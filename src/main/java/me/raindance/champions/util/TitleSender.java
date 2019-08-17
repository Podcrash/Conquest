package me.raindance.champions.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.raindance.champions.Main;
import me.raindance.champions.kits.Skill;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class TitleSender {
    /*
        Progress must be from 0 to 1
     */
    public static WrappedChatComponent chargeUpProgressBar(Skill skill, double progress) {
        if(progress > 1) progress = 1;
        String bar = generateBars("||");
        int size = bar.length() - 1;
        int currentProgress = (int) (size * progress);
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        /*
        String builder = String.format("%s %d:%s%s %s %s%s %d%%",
                skill.getName(), skill.getLevel(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, (int) (100f * progress));
        */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(skill.getName());
        stringBuilder.append(' ');
        stringBuilder.append(skill.getLevel());
        stringBuilder.append(": ");
        stringBuilder.append(ChatColor.BOLD);
        stringBuilder.append(ChatColor.GREEN);
        stringBuilder.append(sprogress);
        stringBuilder.append(ChatColor.RESET);
        stringBuilder.append(ChatColor.BOLD);
        stringBuilder.append(' ');
        stringBuilder.append((int) (100f * progress));
        stringBuilder.append('%');
        return writeTitle(stringBuilder.toString());
    }

    /**
     *
     * @param a must be smaller than b
     * @param b must be larger than a
     * @return
     */
    public static WrappedChatComponent simpleTime(String header, String footer, double a, double b) {
        String bar = generateBars("||");
        int size = bar.length() - 1;
        double percentage = 1D - a/b;
        int currentProgress = (int) (size * percentage);
        currentProgress = (currentProgress > size) ? size : currentProgress;

        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        String builder = ChatColor.BOLD + header + ChatColor.GREEN + sprogress + ChatColor.RESET + ChatColor.BOLD + ' ' + footer;
        return writeTitle(builder);
    }


    public static WrappedChatComponent coolDownBar(Skill skill) {
        String bar = generateBars();
        int size = bar.length() - 1;
        float temp = skill.getCooldown();
        float cooldown = skill.cooldown();
        float product = 1F - cooldown/temp;
        int currentProgress = (int) (size * product);
        currentProgress = (currentProgress > size) ? size : currentProgress;
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        String builder = (!skill.onCooldown()) ? String.format("%s%s%s fully recharged!", ChatColor.GREEN, ChatColor.BOLD, skill.getName())
                : String.format("%s %d:%s%s %s%s%s %.2f s",
                skill.getName(), skill.getLevel(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, cooldown);
        //String variant = skill.getName() + " " + skill.getLevel() + ChatColor.BOLD + ChatColor.GREEN + " " + sprogress + ChatColor.RESET + ChatColor.BOLD + cooldown

        return writeTitle(builder);
    }

    public static String coolDownString(Skill skill) {
        String bar = generateBars();
        int size = bar.length() - 1;
        float temp = skill.getCooldown();
        float cooldown = skill.cooldown();
        float product = 1F - cooldown/temp;
        int currentProgress = (int) (size * product);
        currentProgress = (currentProgress > size) ? size : currentProgress;
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        return (!skill.onCooldown()) ? String.format("%s%s%s fully recharged!", ChatColor.GREEN, ChatColor.BOLD, skill.getName())
                : String.format("%s %d:%s%s %s%s%s %.2f s",
                skill.getName(), skill.getLevel(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, cooldown);
    }

    public static void sendTitle(Player p, WrappedChatComponent iChatBaseComponent) {
        PacketContainer packet = Main.getInstance().getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, iChatBaseComponent);
        packet.getBytes().write(0, (byte) 2);
        try {
            Main.getInstance().getProtocolManager().sendServerPacket(p, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static WrappedChatComponent emptyTitle() {
        return writeTitle("");
    }

    public static WrappedChatComponent writeTitle(String string) {
        return WrappedChatComponent.fromJson("{\"text\":\"" + string + "\"}");
    }

    public static String generateBars(String a) {
        return generateBars().replace("|", a);
    }

    public static String generateBars() {
        return "||||||||||||||||||||";
    }
}
