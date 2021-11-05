package nl.raspen0.stireanniversary;

import nl.raspen0.stireanniversary.commands.*;
import nl.raspen0.stireanniversary.commands.replacements.*;
import nl.raspen0.stireanniversary.sql.SQLHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class StireAnniversary extends JavaPlugin {

    private static StireAnniversary instance;

    public StireAnniversary(){
        instance = this;
    }

    private Logger logger;
    //Worldname, AnniversaryID
    private Map<String, Integer> anniversaryWorlds;
    private Map<Integer, Base> baseMap;
    private Map<UUID, Set<Integer>> playerMap;

    private boolean debugLogging;

    private SQLHandler sqlHandler;
    private AnniversaryWorldListener worldListener;

    public static StireAnniversary getInstance(){
        return instance;
    }

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
        getCommand("sa_reload").setExecutor(new ReloadCommand(this));
        getCommand("sa_spawn").setExecutor(new SpawnCommand(this));

        getCommand("island").setExecutor(new IslandCommand());
        getCommand("enmessage").setExecutor(new LanguageCommand());
        getCommand("survey").setExecutor(new SurveyCommand());
        getCommand("ar").setExecutor(new RankCommand());
        getCommand("calladmin").setExecutor(new CallAdminCommand());
        getCommand("pa").setExecutor(new PACommand());

        if(getConfig().getBoolean("teleportCommands", false)){
            loadBases();
            worldListener = new AnniversaryWorldListener(this);
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
        for(int i : playerMap.get(uuid)){
            Base base = baseMap.get(i);
            System.out.println("Base: "  + base.getBaseID() + ", " +  base.getBaseType().toString() + ", " + base.getAnniversaryWorldID() + ", " + base.getLocation().toString());
        }
        //Retrieve the bases of the player.
        //Set containing baseIDs.

        //If the current world is not an anniversary world.
        if(!isAnniversaryWorld(world.getName())){
            getSALogger().log(Logger.LogType.DEBUG, world.getName() + " is not a anniversary world.");
            return null;
        }

        //Get the Anniversary ID associated with the current world.
        int id = getAnniversaryWorldID(world.getName());

        Set<Integer> baseSet = playerMap.get(uuid);
        for(int i : baseSet){
            //Trying to get base which is not loaded.
            if(!baseMap.containsKey(i)){
                getSALogger().log(Logger.LogType.ERROR, "Trying to load incorrectly loaded base: " + i);
                continue;
            }
            //Get Base.
            Base base = baseMap.get(i);
            System.out.println(base.getLocation());
            System.out.println(base.getBaseType().toString());
            //If the base type is not the one requested, skip it.
            if(!base.getBaseType().equals(baseType)){
                getSALogger().log(Logger.LogType.DEBUG, "Incorrect base type.");
                continue;
            }

            if(getAnniversaryWorldID(base.getLocation().getWorld().getName()) != id){
                getSALogger().log(Logger.LogType.DEBUG, "Incorrect world.");
                continue;
            }

            return base;
        }
        return null;
    }

    public void reload(){
        anniversaryWorlds.clear();
        baseMap.clear();
        playerMap.clear();
        sqlHandler.closeConnection();

        reloadConfig();
        logger = new Logger(this, getConfig().getBoolean("debug.logging"));
        sqlHandler = new SQLHandler(this);
        loadAnniversaryWorldList();

        if(worldListener != null){
            loadBases();
            for(Player p : getServer().getOnlinePlayers()){
                worldListener.playerJoin(p);
            }
        }


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
