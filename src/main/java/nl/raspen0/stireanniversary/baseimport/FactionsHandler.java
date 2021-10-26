package nl.raspen0.stireanniversary.baseimport;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import nl.raspen0.stireanniversary.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class FactionsHandler extends BaseImportHandler {

    FactionsHandler(StireAnniversary plugin) {
        super(plugin);
    }

    public Set<Base> getBases(String worldName, int anniversaryWorldID) {
        Set<Base> baseSet = new HashSet<>();
        for (Faction faction : FactionColl.get().getAll()) {
            String strippedFactionName = ChatColor.stripColor(faction.getName());
            if (strippedFactionName.equals("SafeZone") || strippedFactionName.equals("WarZone") || strippedFactionName.equals("Wilderness")) {
                continue;
            }
            Location factionSpawnLocation = null;
            if (faction.getHome() == null) {
                plugin.getSALogger().log(Logger.LogType.ERROR, "Home not found for Faction: " + faction.getName());
                for (PS ps : BoardColl.get().getChunks(faction))
                    try {
                        if (ps.getChunk() == null) {
                            continue;
                        }
                        if (!ps.getChunk().getWorld().equals(worldName)) {
                            continue;
                        }
                        Location blockLoc = ps.asBukkitChunk().getBlock(0, 0, 0).getLocation();
                        System.out.println(blockLoc);
                        factionSpawnLocation = blockLoc.getWorld().getHighestBlockAt(blockLoc).getLocation();
                    } catch (Exception e) {
                        continue;
                    }
            } else {
                if (!faction.getHome().getWorld().equals(worldName)) {
                    continue;
                }
                factionSpawnLocation = faction.getHome().asBukkitLocation();
            }

            if(factionSpawnLocation == null){
                plugin.getSALogger().log(Logger.LogType.ERROR, "Spawn location not found for Faction: " + faction.getName());
                continue;
            }

            Base base = new Base(factionSpawnLocation, faction.getName(), BaseType.FACTION, anniversaryWorldID);

            for (MPlayer player : faction.getMPlayers()) {
                UUID uuid;
                if(player.getUuid() == null){
                    try {
                        plugin.getSALogger().log(Logger.LogType.FULL, "Retrieving UUID for " + player.getName() + ".");
                        uuid = UUIDFetcher.getUUID(player.getName());
                    } catch (StringIndexOutOfBoundsException | IllegalArgumentException e3) {
                        plugin.getSALogger().log(Logger.LogType.WARNING, "Error retrieving UUID for " + player.getName() + ".");
                        continue;
                    }
                } else {
                    uuid = player.getUuid();
                }
                base.addMember(uuid);
            }

            baseSet.add(base);

        }
        return baseSet;
    }
}
