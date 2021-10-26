package nl.raspen0.stireanniversary;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

public class Logger {

    private final StireAnniversary plugin;
    private boolean debugLogging;
    private boolean isDisabling = false;

    Logger(StireAnniversary plugin, boolean debugLogging) {
        this.plugin = plugin;
        this.debugLogging = debugLogging;
        loadLog();
    }

    public enum LogType {
        FILE(new logFile()),
        CONSOLE(new logConsole()),
        DEBUG(new logDebug()),
        ERROR(new logError()),
        WARNING(new logWarning()),
        FULL(new logFull());

        public LogAction action;

        LogType(LogAction action) {
            this.action = action;
        }
    }

    public void setDebugLogging(boolean debugLogging) {
        this.debugLogging = debugLogging;
    }

    public void setDisabling(boolean disabling){
        this.isDisabling = disabling;
    }

    /**
     * Logs a message depending of the logtype.
     * @param type The type of logging.
     * @param message The message that will be logged.
     * @param color The color for the logged message (only works for the CONSOLE log type).
     */
    public void log(LogType type, String message, ChatColor color) {
        type.action.logMessage(plugin, message, color, debugLogging, isDisabling);
    }

    public void log(LogType type, String message) {
        log(type, message, ChatColor.RESET);
    }

    /**
     * Is used on startup.
     * Creates the StireTweaks directory if it doesn't exist and calls backupFile.
     */
    private void loadLog() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        backupFile("sa");
        backupFile("error");
        if(debugLogging) {
            backupFile("debug");
        }
    }

    /**
     * Copies the log files when they become larger then 500KB.
     * @param filename The name of the log file. (debug will only work if plugin is in debug mode).
     */
    private void backupFile(String filename){
        try {
            File saveTo = new File(plugin.getDataFolder(), filename + ".log");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
                return;
            }
            if(saveTo.length() >= 300000){
                File backupFolder = new File(plugin.getDataFolder(), "backups");
                if (!backupFolder.exists()) {
                    backupFolder.mkdir();
                }
                SimpleDateFormat format = new SimpleDateFormat("[dd-MM-yy]", Locale.US);
                Date date = new Date();
                String dateformatted = format.format(date);
                saveTo.renameTo(new File(backupFolder, filename + "-" + dateformatted + ".log"));
                saveTo.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

abstract class LogAction {
    abstract void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling);

    void logMessage(StireAnniversary plugin, String message, boolean debugLogging, boolean isDisabling){
        logMessage(plugin, message, ChatColor.RESET, debugLogging, isDisabling);
    }

    protected void logToFile(String fileName, StireAnniversary plugin, String message){
        try {
            File file = new File(plugin.getDataFolder(), fileName);
            if(!file.exists()){
                createFile(plugin, file);
            }
            FileWriter fw = new FileWriter(new File(plugin.getDataFolder(), fileName), true);
            PrintWriter pw = new PrintWriter(fw);
            SimpleDateFormat format = new SimpleDateFormat("[dd-MMM-yy]-[HH:mm:ss]", Locale.US);
            Date date = new Date();
            String dateformatted = format.format(date);
            pw.println(dateformatted + " - " + message);
            pw.close();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "[SA] Could not initialize logger!");
            plugin.getLogger().log(Level.SEVERE, "[SA] Unloading StireAnniversary.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private boolean createFile(StireAnniversary plugin, File file){
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            return file.createNewFile();

        } catch (IOException e){
            return false;
        }
    }
}

/**
 * Logs the given message to sa.log.
 */
class logFile extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        if(plugin.getServer().isPrimaryThread() && !isDisabling){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> logToFile("sa.log", plugin, message));
        } else {
            logToFile("sa.log", plugin, message);
        }
    }
}

/**
 * Logs the given message to sa.log and the console.
 */
class logFull extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        if(plugin.getServer().isPrimaryThread() && !isDisabling){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> logToFile("sa.log", plugin, message));
        } else {
            logToFile("sa.log", plugin, message);
        }
        //plugin.getLogger().log(Level.INFO, message);
        Bukkit.getConsoleSender().sendMessage(color + "[SA] " + message);
    }
}

/**
 * Logs the given message to the console.
 */
class logConsole extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        Bukkit.getConsoleSender().sendMessage(color + "[SA] " + message);
    }
}

/**
 * Logs the given message to debug.log and the console (SA-Debug)
 */
class logDebug extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        if (debugLogging) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SA-Debug] " + message);
            if(plugin.getServer().isPrimaryThread() && !isDisabling){
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> logToFile("debug.log", plugin, message));
            } else {
                logToFile("debug.log", plugin, message);
            }
        }
    }
}

/**
 * Logs the given error message to sa.log and the console.
 */
class logError extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        //Using Bukkit Logger to set the log type to SEVERE.
        plugin.getLogger().log(Level.SEVERE, message);
        if(plugin.getServer().isPrimaryThread() && !isDisabling){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> logToFile("error.log", plugin, message));
        } else {
            logToFile("error.log", plugin, message);
        }
    }
}

/**
 * Logs the given warning message to sa.log and the console.
 */
class logWarning extends LogAction {
    @Override
    public void logMessage(StireAnniversary plugin, String message, ChatColor color, boolean debugLogging, boolean isDisabling) {
        //Using Bukkit Logger to set the log type to WARNING.
        plugin.getLogger().log(Level.WARNING, message);
        if(plugin.getServer().isPrimaryThread() && !isDisabling){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> logToFile("sa.log", plugin, message));
        } else {
            logToFile("sa.log", plugin, message);
        }
    }
}

	

