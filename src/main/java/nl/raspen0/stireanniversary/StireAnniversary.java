package nl.raspen0.stireanniversary;

import nl.raspen0.stireanniversary.commands.HomeCommand;
import nl.raspen0.stireanniversary.commands.ImportCommand;
import nl.raspen0.stireanniversary.commands.TeleportCommand;
import nl.raspen0.stireanniversary.sql.SQLHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class StireAnniversary extends JavaPlugin {

    private Logger logger;
    //Worldname, AnniversaryID
    private Map<String, Integer> anniversaryWorlds;
    private Map<Integer, Base> baseMap;
    private Map<UUID, Set<Integer>> playerMap;

    private boolean debugLogging;

    private SQLHandler sqlHandler;

    @Override
    public void onEnable() {
        anniversaryWorlds = new HashMap<>();
        baseMap = new HashMap<>();
        playerMap = new HashMap<>();
        saveDefaultConfig();
        debugLogging = getConfig().getBoolean("debug.logging", false);
        logger = new Logger(this, debugLogging);
        sqlHandler = new SQLHandler(this);
        loadAnniversaryWorldList();

        if(getConfig().getBoolean("teleportCommands", false)){
            loadBases();
            AnniversaryWorldListener worldListener = new AnniversaryWorldListener(this);
            getServer().getPluginManager().registerEvents(worldListener,  this);

            //Is plugin was enabled or reloaded using PlugMan.
            if(getServer().getOnlinePlayers().size() > 0){
                for(Player p : getServer().getOnlinePlayers()){
                    worldListener.playerJoin(p);
                }
            }

            if(!getServer().getPluginManager().isPluginEnabled("StireTweaks")){
                getSALogger().log(Logger.LogType.ERROR, "StireTweaks not found, cannnot enable teleport commands.");
            } else {
                HomeCommand homeCommand = new HomeCommand(this);
                TeleportCommand teleportCommand = new TeleportCommand(this);
                getCommand("sa_home").setExecutor(homeCommand);
                getCommand("sa_teleport").setExecutor(teleportCommand);
                getCommand("sa_home").setTabCompleter(homeCommand);
                getCommand("sa_teleport").setTabCompleter(teleportCommand);
            }
        }

        if(getConfig().getBoolean("importCommand", true)){
            ImportCommand importCommand = new ImportCommand(this);
            getCommand("sa_import").setExecutor(importCommand);
            getCommand("sa_import").setTabCompleter(importCommand);
        }


    }

    private void loadAnniversaryWorldList(){
        for(String key : getConfig().getConfigurationSection("anniversaryWorlds").getKeys(false)){
            for(String world : getConfig().getStringList("anniversaryWorlds." + key)){
                getSALogger().log(Logger.LogType.CONSOLE, "Enabling Anniversary mode for world: " + world + ", ID: " + key);
                anniversaryWorlds.put(world, Integer.parseInt(key));
            }

        }
    }

    public boolean debugLoggingEnabled() {
        return debugLogging;
    }

    private void loadBases(){
        baseMap = sqlHandler.loadBases();
    }

    public void loadPlayer(UUID uuid){
        Set<Integer> baseSet = sqlHandler.loadPlayerBases(uuid);
        if(!baseSet.isEmpty()){
            playerMap.put(uuid, baseSet);
        }
    }

    public Map<Integer, Base> getBases() {
        return baseMap;
    }

    public Base getBase(UUID uuid, World world, BaseType baseType){
        Set<Integer> baseSet = playerMap.get(uuid);
        for(int i : baseSet){
            Base base = baseMap.get(i);
            System.out.println(base.getLocation());

            if(!base.getBaseType().equals(baseType)){
                getSALogger().log(Logger.LogType.DEBUG, "Incorrect base type.");
                continue;
            }

            if(!isAnniversaryWorld(world.getName())){
                getSALogger().log(Logger.LogType.DEBUG, world.getName() + " is not a anniversary world.");
                continue;
            }

            int id = getAnniversaryWorldID(world.getName());

            for(Map.Entry<String, Integer> e : anniversaryWorlds.entrySet()){
                if(e.getValue() != id){
                    getSALogger().log(Logger.LogType.DEBUG, "ID mismatch: " + id + " - " + e.getValue());
                    continue;
                }
                if(!base.getLocation().getWorld().getName().equals(world.getName())){
                    getSALogger().log(Logger.LogType.DEBUG, "World mismatch: " + base.getLocation().getWorld().getName() + " - " + world.getName());
                    continue;
                }
                return base;
            }
        }
        return null;
    }

    public boolean isAnniversaryWorld(String world){
        return anniversaryWorlds.containsKey(world);
    }

    public Integer getAnniversaryWorldID(String world){
        return anniversaryWorlds.get(world);
    }

    @Override
    public void onDisable() {
        logger.setDisabling(true);
        anniversaryWorlds.clear();
        baseMap.clear();
        playerMap.clear();
    }

    public Logger getSALogger(){
        return logger;
    }

    public SQLHandler getSqlHandler() {
        return sqlHandler;
    }
}
