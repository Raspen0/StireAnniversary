package nl.raspen0.stireanniversary.baseimport;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import nl.raspen0.stireanniversary.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TownyHandler extends BaseImportHandler {

    TownyHandler(StireAnniversary plugin) {
        super(plugin);
    }

    public Set<Base> getBases(String worldName, int anniversaryWorldID) {
        Set<Base> baseSet = new HashSet<>();
        for (Town town : TownyUniverse.getInstance().getDataSource().getTowns()) {
            try {
                if (!town.getSpawn().getWorld().getName().equals(worldName)) {
                    continue;
                }
                if(!town.hasSpawn()){
                    plugin.getSALogger().log(Logger.LogType.ERROR, "Town does not have a spawn: " + town.getUUID().toString());
                    continue;
                }
                Base base = new Base(town.getSpawn(), town.getName(), BaseType.TOWN, -1, anniversaryWorldID);

                for (Resident resident : town.getResidents()) {
                    UUID uuid;
                    if(resident.getUUID() == null) {
                        try {
                            plugin.getSALogger().log(Logger.LogType.FULL, "Retrieving UUID for " + resident.getName() + ".");
                            uuid = UUIDFetcher.getUUID(resident.getName());
                        } catch (StringIndexOutOfBoundsException | IllegalArgumentException e3) {
                            plugin.getSALogger().log(Logger.LogType.WARNING, "Error retrieving UUID for " + resident.getName() + ".");
                            continue;
                        }
                    } else {
                        uuid = resident.getUUID();
                    }
                    base.addMember(uuid);
                }
                baseSet.add(base);
            } catch (TownyException e) {
                plugin.getSALogger().log(Logger.LogType.ERROR, "Error processing town: " + town.getName());
                e.printStackTrace();
                continue;
            }



        }
        return baseSet;
    }
}
