package nl.raspen0.stireanniversary;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldGuardRegionHandler {

    /**
     * Checks if the give location is inside a spawn region.
     *
     * @param location The location to be checked.
     * @param plugin The plugin instance.
     *
     * @return True if the give location is in a spawn region, otherwise false.
     */
    public static boolean isInWorldGuardRegion(Location location, StireAnniversary plugin){
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")){
            plugin.getSALogger().log(Logger.LogType.WARNING, "WorldGuard not found, skipping check.");
            return false;
        }
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        for(ProtectedRegion region : set){
            if(region.getId().equalsIgnoreCase("spawn")){
                return true;
            }
        }
        return false;
    }
}
