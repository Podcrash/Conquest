package me.raindance.champions.commands;

import me.raindance.champions.game.Game;
import me.raindance.champions.game.GameManager;
import me.raindance.champions.game.GameType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NewGameCommand extends CommandBase{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("Champions.host") && args.length == 1) {
            if(GameManager.getGameCount() >= 5) {
                sender.sendMessage("please dont try to create a ton of games, thanks.");
                return true;
            }
            switch(args[0]) {
                case "DOM":
                    Game game = GameManager.createGame(Long.toString(System.currentTimeMillis()), GameType.DOM);
                    sender.sendMessage("Created a new game of type DOM with the ID " + game.getId()
                            + ". You can join this game with the command '/join " + game.getId() + "'.");
                    return true;
                case "CTF":
                    sender.sendMessage("LORD BLOBBY HAS APPEARED - YOU HAVE BEEN ENLIGHTENED");
                    return true;
            }
        }
        return false;
    }
}
