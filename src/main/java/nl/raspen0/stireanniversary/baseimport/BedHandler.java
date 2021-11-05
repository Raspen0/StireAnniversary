package nl.raspen0.stireanniversary.baseimport;

import nl.raspen0.stireanniversary.Base;
import nl.raspen0.stireanniversary.BaseType;
import nl.raspen0.stireanniversary.StireAnniversary;
import nl.raspen0.stireanniversary.WorldGuardRegionHandler;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;

public class BedHandler extends BaseImportHandler{

    BedHandler(StireAnniversary plugin){
        super(plugin);
    }

    public Set<Base> getBases(String worldName, int anniversaryWorldID) {
        Set<Base> baseSet = new HashSet<>();

        for(OfflinePlayer p : plugin.getServer().getOfflinePlayers()){
            Location bedLocation = p.getBedSpawnLocation();
            if(bedLocation == null || bedLocation.getWorld() == null){
                continue;
            }
            if(!bedLocation.getWorld().getName().equals(worldName)){
                continue;
            }

            if(WorldGuardRegionHandler.isInWorldGuardRegion(bedLocation, plugin)){
                continue;
            }

            Base base = new Base(bedLocation, BaseType.BED, -1, anniversaryWorldID);
            base.addMember(p.getUniqueId());
            baseSet.add(base);
        }
        return baseSet;
    }
}
