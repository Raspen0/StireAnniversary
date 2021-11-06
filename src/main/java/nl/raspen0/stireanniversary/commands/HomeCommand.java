package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.*;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements TabExecutor {

    private final StireAnniversary plugin;

    public HomeCommand(StireAnniversary plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //sa_home <type> <@player>

        Player target;

        if(args.length < 2){
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
                return true;
            }
            target = player;
        } else {
            target = Bukkit.getPlayer(args[1]);
            if(target == null){
                sender.sendMessage(ChatColor.RED + "Invalid player.");
                return true;
            }
        }


        Language language = LanguageHandler.getLanguage(target.getUniqueId());
        if (args.length < 1) {
            target.sendMessage(StringList.HOME_NO_TYPE.get(language));
            target.sendMessage(StringList.HOME_VALID_TYPES.get(language));
            return true;
        }

        Base base;
        String type = args[0].toLowerCase();

        switch (type) {
            case "claimed" -> {
                type = language == Language.NL ? "faction of dorp" : "faction or town";
                base = plugin.getBase(target.getUniqueId(), target.getWorld(), BaseType.FACTION);
                if (base == null) {
                    base = plugin.getBase(target.getUniqueId(), target.getWorld(), BaseType.TOWN);
                }
            }
            case "home" -> {
                type = "/home";
                base = plugin.getBase(target.getUniqueId(), target.getWorld(), BaseType.ESSENTIALS);
            }
            case "bed" -> base = plugin.getBase(target.getUniqueId(), target.getWorld(), BaseType.BED);
            default -> {
                target.sendMessage(language == Language.ENG ? "Unknown home type." : "Onbekend home type.");
                return true;
            }
        }

        if (base == null) {
            if (language == Language.NL) {
                target.sendMessage(ChatColor.RED + "Je hebt geen " + type + " gehad in deze wereld.");
            } else {
                target.sendMessage(ChatColor.RED + "You did not have a " + type + " in this world.");
            }
            return true;
        }

        new TeleportHandler(plugin).teleportPlayer(target, sender, base);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], List.of("claimed", "home", "bed"), list);
            return list;
        }
        return list;
    }
}
