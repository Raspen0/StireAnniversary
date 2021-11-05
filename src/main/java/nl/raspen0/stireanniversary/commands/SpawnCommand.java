package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.*;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final StireAnniversary plugin;

    public SpawnCommand(StireAnniversary plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage(StringList.ONLY_PLAYER.get(Language.ENG));
            return true;
        }

        String world = player.getWorld().getName();

        if(!plugin.isAnniversaryWorld(world)){
            Language lang = LanguageHandler.getLanguage(player.getUniqueId());
            sender.sendMessage(StringList.SPAWN_COMMAND_INCORRECT_WORLD.get(lang));
            return true;
        }

        //Get the Anniversary ID associated with the current world.
        int id = plugin.getAnniversaryWorldID(world);

        Location loc = getSpawnLocation(id);
        player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND);

        return true;
    }


    //TODO: Move to config file.
    private Location getSpawnLocation(int id){
        return switch (id) {
            case 1 -> new Location(Bukkit.getWorld("world1"), 247.6438504902772, 185.0, 310.21953567170686,
                    178.34764099121094F, 8.250032424926758F);
            case 2 -> new Location(Bukkit.getWorld("worldv2"), -205.87107341724027, 192.0, 172.51120842546686,
                    89.99824523925781F, -2.249992847442627F);
            case 3 -> new Location(Bukkit.getWorld("spawn3"), 112.59930033194334, 178.0, 103.60103864066129,
                    91.35012817382812F, 1.3500005006790161F);
            case 4 -> new Location(Bukkit.getWorld("survival4"), 220.62132609156785, 139.0, 141.49324159340338,
                    89.98226928710938F, 1.3498955965042114F);
            case 5 -> new Location(Bukkit.getWorld("survival5"), 158.3005512711826, 119.0, 243.48762522133762,
                    88.95832061767578F, 0.5995445847511292F);
            default -> null;
        };
    }
}
