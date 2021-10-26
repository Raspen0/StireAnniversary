package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.*;
import nl.raspen0.stiretweaks.language.Language;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeleportCommand implements TabExecutor {

    private final StireAnniversary plugin;

    public TeleportCommand(StireAnniversary plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Language lang = sender instanceof Player ? LanguageHandler.getLanguage(((Player) sender).getUniqueId()) : Language.ENG;
        if(!sender.hasPermission("stireanniversary.command.teleport")){
            sender.sendMessage(StringList.NO_PERMISSION.get(lang));
            return true;
        }
        //0: basename / id.
        //1: target (optional)

        if(args.length < 1){
            sender.sendMessage(StringList.NOT_ENOUGH_ARGS.get(lang));
            return true;
        }

        if(args[0].equalsIgnoreCase("base")){
            baseTeleport(sender, args, lang);
        } else if(args[0].equalsIgnoreCase("player")){
            playerTeleport(sender, args, lang);
        } else {
            sender.sendMessage(StringList.TELEPORT_INVALID_TYPE.get(lang));
        }

        return true;
    }

    private void baseTeleport(CommandSender sender, String[] args, Language lang){
        //sa_teleport base <baseName/ID> <@target>
        Player target;

        if(args.length < 2){
            sender.sendMessage(StringList.NOT_ENOUGH_ARGS.get(lang));
            return;
        }

        if(args.length < 3){
            if(!(sender instanceof Player player)){
                sender.sendMessage(StringList.ONLY_PLAYER.get(lang));
                return;
            }
            target = player;
        } else {
            Player player = plugin.getServer().getPlayer(args[2]);
            if(player == null){
                sender.sendMessage(StringList.PLAYER_NOT_FOUND.get(lang));
                return;
            }
            target = player;
        }

        Base base = null;

        for(Map.Entry<Integer, Base> entry : plugin.getBases().entrySet()){
            if(entry.getValue().getName() != null && entry.getValue().getName().equals(args[1])){
                base = entry.getValue();
                break;
            }
            try {
                int id = Integer.parseInt(args[1]);
                if(entry.getKey() == id){
                    base = entry.getValue();
                    break;
                }
            } catch (NumberFormatException ignored){
            }
        }

        new TeleportHandler(plugin).teleportPlayer(target, sender, base);
    }

    private void playerTeleport(CommandSender sender, String[] args, Language lang){
        //sa_teleport player <player> <basetype> <@world> <@target>

        Player target;

        if(args.length < 5){
            if(!(sender instanceof Player player)){
                sender.sendMessage(StringList.ONLY_PLAYER.get(lang));
                return;
            }
            target = player;
        } else {
            Player player = plugin.getServer().getPlayer(args[4]);
            if(player == null){
                sender.sendMessage(StringList.PLAYER_NOT_FOUND.get(lang));
                return;
            }
            target = player;
        }

        World world;

        if(args.length < 4){
            world = target.getWorld();
        } else {
            world = Bukkit.getWorld(args[3]);
            if(world == null){
                sender.sendMessage(StringList.WORLD_NOT_FOUND.get(lang));
                return;
            }
        }
        new TeleportHandler(plugin).teleportPlayer(target, sender, args[2].toUpperCase(), world);
    }



    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        //sa_teleport base <baseName/ID> <@target>
        //sa_teleport player <player>    <basetype> <@world> <@target>
        List<String> list = new ArrayList<>();
        if(args.length == 1){
            StringUtil.copyPartialMatches(args[0], List.of("base", "player"), list);
            return list;
        }
        if(args.length == 2){
            if(args[0].equals("base")){
                return list;
            }
            if(args[0].equals("player")){
                return null;
            }
        }

        if(args.length == 3){
            if(args[0].equals("base")){
                return null;
            }
            if(args[0].equals("player")){
                String[] values = Arrays.stream(BaseType.values()).map(Enum::name).map(String::toLowerCase).toArray(String[]::new);
                StringUtil.copyPartialMatches(args[2], List.of(values), list);
                return null;
            }
        }

        if(args.length == 4){
            if(args[0].equals("player")){
                String[] values = plugin.getServer().getWorlds().stream().map(World::getName).toArray(String[]::new);
                StringUtil.copyPartialMatches(args[3], List.of(values), list);
                return null;
            }
        }

        if(args.length == 5){
            if(args[0].equals("player")){
                return null;
            }
        }
        return list;
    }
}
