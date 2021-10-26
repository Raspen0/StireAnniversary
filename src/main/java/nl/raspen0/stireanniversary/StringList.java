package nl.raspen0.stireanniversary;

import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.ChatColor;


public enum StringList {

    ONLY_PLAYER(ChatColor.RED + "This command can only be used by a player.", ChatColor.RED + "Dit commando kan alleen door een speler worden uitgevoert."),
    PLAYER_NOT_FOUND(ChatColor.RED + "Player not found.", ChatColor.RED + "Speler niet gevonden."),
    WORLD_NOT_FOUND(ChatColor.RED + "World not found.", ChatColor.RED + "wereld niet gevonden."),
    NO_PERMISSION(ChatColor.RED + "You don't have permission to use this command!", ChatColor.RED + "Je hebt geen rechten om dit commando uit te voeren!"),
    NOT_ENOUGH_ARGS(ChatColor.RED + "Not enough command arguments.", ChatColor.RED + "Niet genoeg commando argumenten."),

    HOME_NO_TYPE(ChatColor.RED + "Je moet een type home selecteren. Gebruik: " + ChatColor.YELLOW + "/sa_home <type>" + ChatColor.RED + ".",
            ChatColor.RED + "Je have to select a home type. Use: " + ChatColor.YELLOW + "/sa_home <type>" + ChatColor.RED + "."),
    HOME_VALID_TYPES(ChatColor.RED + "Types: " + ChatColor.YELLOW + "claimed" + ChatColor.RED + " (Faction/Town spawn point), " +
            ChatColor.YELLOW + "home" + ChatColor.RED + " (/home), " + ChatColor.YELLOW + "bed" + ChatColor.RED + " (Your bed spawnpoint).",
            ChatColor.RED + "Types: " + ChatColor.YELLOW + "claimed" + ChatColor.RED + " (Faction/Town spawn punt), " +
                    ChatColor.YELLOW + "home" + ChatColor.RED + " (/home), " + ChatColor.YELLOW + "bed" + ChatColor.RED + " (Je bed spawnpunt in die wereld)."),
    TELEPORTED_OTHER( ChatColor.GREEN + " has been teleported.", ChatColor.GREEN + " is geteleporteed."),
    TELEPORT_INVALID_TYPE(ChatColor.RED + "Invalid teleport type (base/player)", ChatColor.RED + "Ongeldig teleportatie type (base/player)"),

    BASE_TELEPORT_MESSAGE_NAME(ChatColor.AQUA + "Welcome to " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Welkom bij " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + "."),
    BASE_TELEPORT_MESSAGE_NO_NAME(ChatColor.AQUA + "Welcome to your " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Welkom bij je " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + "."),

    FACTION("faction"),
    TOWN("town", "dorp"),

    ;

    String english;
    String dutch;

    StringList(String english, String dutch) {
        this.english = english;
        this.dutch = dutch;
    }

    StringList(String english) {
        this(english, english);
    }


    public String get(Language lang) {
        if(lang == null){
            return ChatColor.RED + "Could not get language, please set your language using /lang";
        }
        if(lang.equals(Language.NL)){
            return dutch;
        } else {
            return english;
        }
    }
}
