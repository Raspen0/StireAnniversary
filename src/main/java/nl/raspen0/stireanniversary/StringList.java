package nl.raspen0.stireanniversary;

import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.ChatColor;


public enum StringList {

    ONLY_PLAYER(ChatColor.RED + "This command can only be used by a player.", ChatColor.RED + "Dit commando kan alleen door een speler worden uitgevoerd."),
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
    TELEPORTED_OTHER( ChatColor.GREEN + " has been teleported.", ChatColor.GREEN + " is geteleporteerd."),
    TELEPORT_INVALID_TYPE(ChatColor.RED + "Invalid teleport type (base/player)", ChatColor.RED + "Ongeldig teleportatie type (base/player)"),

    TELEPORT_RETURN_GLOBAL_SPAWN(ChatColor.AQUA + "To return to the server spawn, use: " + ChatColor.YELLOW + "/spawn" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Om terug te gaan naar het server spawn, gebruik: " + ChatColor.YELLOW + "/spawn" + ChatColor.AQUA + "."),
    TELEPORT_RETURN_LOCAL_SPAWN(ChatColor.AQUA + "To return to the spawn of this world, use: " + ChatColor.YELLOW + "/sa_spawn" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Om terug te gaan naar het spawn van deze wereld, gebruik: " + ChatColor.YELLOW + "/sa_spawn" + ChatColor.AQUA + "."),

    BASE_TELEPORT_MESSAGE_NAME(ChatColor.AQUA + "Welcome to " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Welkom bij " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + "."),
    BASE_TELEPORT_MESSAGE_NO_NAME(ChatColor.AQUA + "Welcome to your " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + ".",
            ChatColor.AQUA + "Welkom bij je " + ChatColor.YELLOW + "{0}" + ChatColor.AQUA + "."),

    SPAWN_COMMAND_INCORRECT_WORLD(ChatColor.RED + "You are currently not in a anniversary world.",
            ChatColor.RED + "Je bent op het moment niet in een jubileum wereld."),

    FACTION("faction"),
    TOWN("town", "dorp"),

    COMMAND_REPLACEMENT_LANGUAGE(ChatColor.AQUA +
            "On the original server this command would have changed your language, just like /lang does now.",
            ChatColor.AQUA + "Op de originele server veranderde dit commando je taal, net zoals /lang dat nu doet."),

    COMMAND_REPLACEMENT_ISLAND(ChatColor.AQUA +
            "On the original server this command would be used to access your SkyBlock island. This feature was sadly " +
            "removed due to people not really using it.",
                                 ChatColor.AQUA + "Op de originele server werd dit command gebruikt om bij je SkyBlock eiland te komen." +
                                         "Skyblock is helaas weggehaald omdat bijna niemand het gebruikte."),

    COMMAND_REPLACEMENT_RANK(ChatColor.AQUA +
            "On the original server this command would be used to check your playtime and how would have until you ranked up. " +
            "This is now handled by /playtime. Back then we used the AutoRank plugin, but due to a lot of issues with playtime counting we switched to " +
            "a custom made plugin.",
            ChatColor.AQUA + "Op de originele server werd dit command gebruikt om je speeltijd na te kijken en te zien wanneer je rank omhoog gaat. " +
                    "Dit wordt nu gedaan door /playtime. In die tijd gebruikten we de AutoRank plugin, maar door veel problemen met het tellen van de speeltijd " +
                    "zijn we overgegaan naar een zelfgemaakte plugin."),

    COMMAND_REPLACEMENT_SURVEY(ChatColor.AQUA +
            "There is currently no survey.",
            ChatColor.AQUA + "Er is op het moment geen enquête."),

    COMMAND_REPLACEMENT_CALLADMIN(ChatColor.AQUA +
            "On the original server this command would send a mobile notification to me (raspen0). " +
            "This was removed due to the app not being updated anymore.",
            ChatColor.AQUA + "Op de originele server werd dit commando gebruikt om mobiele notificaties naar mij (raspen0) te sturen. " +
                    "Dit was weggehaald omdat de app niet meer bijgewerkt werd."),

    COMMAND_REPLACEMENT_PVP(ChatColor.AQUA +
            "On the original server this command was used for the PvP Arena.",
            ChatColor.AQUA + "Op de originele server werd dit commando gebruikt voor de PvP arena."),

    COMMAND_REPLACEMENT_SKYBLOCK_SHOP(ChatColor.BLUE + "On the original server with this command you could open the Skyblock shop. " +
            "Here you could buy items to help you such as saplings and extra dirt. This disappeared because Skyblock was " +
            "removed due almost never being played.",
            ChatColor.BLUE + "Op de originele server kon je met dit commando de Skyblock winkel openen. " +
                    "Hier kon je blokken kopen die je konden helpen zoals kiemplanten (saplings) extra aarde. " +
                    "Dit verdween doordat Skyblock was weggehaald omdat bijna niemand het speelde.")

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
