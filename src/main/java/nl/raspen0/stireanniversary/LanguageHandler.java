package nl.raspen0.stireanniversary;

import nl.raspen0.stiretweaks.StireTweaks;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LanguageHandler {

    private LanguageHandler(){}

    public static Language getLanguage(CommandSender sender){
        if(!(sender instanceof Player player)){
            return Language.ENG;
        }
        return getLanguage(player.getUniqueId());
    }

    public static Language getLanguage(UUID uuid){
        StireTweaks stireTweaks = (StireTweaks) Bukkit.getPluginManager().getPlugin("StireTweaks");
        return stireTweaks.getPlayerDataHandler().getPlayerData(uuid).getLanguage();
    }

}
