package nl.raspen0.stireanniversary;

import nl.raspen0.stiretweaks.StireTweaks;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.Bukkit;

import java.util.UUID;

public class LanguageHandler {

    private LanguageHandler(){}

    public static Language getLanguage(UUID uuid){
        StireTweaks stireTweaks = (StireTweaks) Bukkit.getPluginManager().getPlugin("StireTweaks");
        return stireTweaks.getPlayerDataHandler().getPlayerData(uuid).getLanguage();
    }

}
