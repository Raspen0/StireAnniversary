package nl.raspen0.stireanniversary.baseimport;

import com.earth2me.essentials.Essentials;
import nl.raspen0.stireanniversary.*;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EssentialsHandler extends BaseImportHandler {

    EssentialsHandler(StireAnniversary plugin) {
        super(plugin);
    }

    public Set<Base> getBases(String worldName, int anniversaryWorldID) {
        Set<Base> baseSet = new HashSet<>();
        Essentials ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
        final File userdir = new File(ess.getDataFolder(), "userdata");
        if (!userdir.exists()) {
            plugin.getSALogger().log(Logger.LogType.ERROR, "userdata directory does not exist.");
            return baseSet;
        }
        for (final File file : userdir.listFiles()) {
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Processing " + file.getName());
            if (!file.getName().endsWith(".yml")) {
                plugin.getSALogger().log(Logger.LogType.DEBUG, "Skipping non YAML file: " + file.getName());
                continue;
            }
            FileConfiguration userFile = YamlConfiguration.loadConfiguration(file);
            if (!userFile.contains("homes.home")) {
                plugin.getSALogger().log(Logger.LogType.DEBUG, "Userfile does not contain home.");
                continue;
            }
            ConfigurationSection homeSection = userFile.getConfigurationSection("homes.home");

            //Log home section to console if debug logging is enabled.
            if (plugin.debugLoggingEnabled()) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for (String s : homeSection.getKeys(false)) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append(s).append(": ").append(homeSection.get(s));
                    first = false;
                }
                plugin.getSALogger().log(Logger.LogType.DEBUG, builder.toString());
            }

            String world = homeSection.getString("world", "null");
            if (plugin.getServer().getWorld(world) == null) {
                plugin.getSALogger().log(Logger.LogType.ERROR, "Unknown world: " + world + " for player: " + userFile.getString("last-account-name"));
                continue;
            }
            if (!world.equals(worldName)) {
                plugin.getSALogger().log(Logger.LogType.DEBUG, "Worldname not equal.");
                continue;
            }
            Location loc = new Location(plugin.getServer().getWorld(world), homeSection.getDouble("x"),
                    homeSection.getDouble("y"), homeSection.getDouble("z"),
                    (float) homeSection.getDouble("yaw"), (float) homeSection.getDouble("pitch"));
            if (WorldGuardRegionHandler.isInWorldGuardRegion(loc, plugin)) {
                plugin.getSALogger().log(Logger.LogType.DEBUG, "Is in spawn region.");
                continue;
            }
            Base base = new Base(loc, BaseType.ESSENTIALS, -1, anniversaryWorldID);
            base.addMember(UUID.fromString(file.getName().substring(0, file.getName().length() - 4)));
            baseSet.add(base);
        }
        return baseSet;
    }
}
