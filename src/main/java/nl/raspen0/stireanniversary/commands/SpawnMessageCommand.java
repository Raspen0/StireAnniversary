package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.LanguageHandler;
import nl.raspen0.stireanniversary.StringList;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnMessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length < 1){
            sender.sendMessage(StringList.NOT_ENOUGH_ARGS.get(LanguageHandler.getLanguage(sender)));
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null){
            sender.sendMessage(StringList.PLAYER_NOT_FOUND.get(LanguageHandler.getLanguage(sender)));
            return true;
        }
        player.sendMessage(StringList.TELEPORT_RETURN_LOCAL_SPAWN.get(LanguageHandler.getLanguage(player)));
        return true;
    }
}
