package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.Base;
import nl.raspen0.stireanniversary.BaseType;
import nl.raspen0.stireanniversary.StireAnniversary;
import nl.raspen0.stireanniversary.baseimport.BaseImportFactory;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ImportCommand implements TabExecutor {

    private final StireAnniversary plugin;

    public ImportCommand(StireAnniversary plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("stireanniversary.migrate")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You must specify a base type.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must specify a world name.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "You must specify a anniversary world ID number (Ex: 1 for Stirebuild 1).");
            return true;
        }

        int anniversaryWorldID;

        try {
            anniversaryWorldID = Integer.parseInt(args[2]);
        } catch (NumberFormatException e){
            sender.sendMessage(ChatColor.RED + "The anniversary world ID must be a number.");
            return true;
        }

        if (!EnumUtils.isValidEnum(BaseType.class, args[0].toUpperCase())) {
            sender.sendMessage(ChatColor.RED + "Invalid base type.");
            return true;
        }

        BaseType baseType = BaseType.valueOf(args[0].toUpperCase());
        Set<Base> baseSet = BaseImportFactory.getImportHandler(baseType, plugin).getBases(args[1], anniversaryWorldID);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getSqlHandler().addBases(baseSet)) {
                sender.sendMessage(ChatColor.GREEN + "Base import complete.");
            } else {
                sender.sendMessage(ChatColor.RED + "Error importing bases.");
            }

        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (!sender.hasPermission("stireanniversary.migrate")) {
                return null;
            }
            List<String> list = new ArrayList<>();
            String[] values = Arrays.stream(BaseType.values()).map(Enum::name).map(String::toLowerCase).toArray(String[]::new);
            StringUtil.copyPartialMatches(args[0], List.of(values), list);
            return list;
        }

        return null;
    }
}
