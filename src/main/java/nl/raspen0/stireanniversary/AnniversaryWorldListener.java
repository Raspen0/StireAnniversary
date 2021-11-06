package nl.raspen0.stireanniversary;

import net.ess3.api.events.UserBalanceUpdateEvent;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AnniversaryWorldListener implements Listener {

    private final StireAnniversary plugin;
    private final Set<UUID> playerSet;

    AnniversaryWorldListener(StireAnniversary plugin){
        this.plugin = plugin;
        playerSet = new HashSet<>();
    }

    @EventHandler
    private void storageClick(InventoryClickEvent event){
        if(plugin.debugLoggingEnabled()){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Storage click: " +
                    (event.getClickedInventory() == null ? "null" : event.getClickedInventory().getType())  + ".");
        }
        if(event.getClickedInventory() == null || event.getCurrentItem() == null){
            return;
        }

        if(!playerSet.contains(event.getWhoClicked().getUniqueId())){
            return;
        }

        if(event.getWhoClicked().hasPermission("stireanniversary.bypass.chest")){
            return;
        }

        if(event.getClickedInventory().getType().equals(InventoryType.WORKBENCH)){
            return;
        }

        if(event.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            switch (event.getClick()) {
                case DROP, LEFT, RIGHT, MIDDLE, NUMBER_KEY -> {}
                default -> {
                    if (!event.getInventory().getType().equals(InventoryType.PLAYER) && !event.getInventory().getType().equals(InventoryType.CRAFTING)) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {
            event.setCancelled(true);
            event.getWhoClicked().setItemOnCursor(event.getCurrentItem().clone());
        }
    }

    @EventHandler
    private void storageDrag(InventoryDragEvent event){
        if(plugin.debugLoggingEnabled()){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Storage drag: " + event.getInventory().getType()  + ".");
        }
        if(!playerSet.contains(event.getWhoClicked().getUniqueId())){
            return;
        }

        if(event.getWhoClicked().hasPermission("stireanniversary.bypass.chest")){
            return;
        }

        if(event.getInventory().getType().equals(InventoryType.WORKBENCH)){
            return;
        }

        for(int slot : event.getRawSlots()){
            if(slot < 54){
                event.setCancelled(true);
                return;
            }
        }
    }

    //Negate all money changes in anniversary worlds.
    @EventHandler
    private void userBalance(UserBalanceUpdateEvent event){
        if(playerSet.contains(event.getPlayer().getUniqueId())){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Negating balance change for " + event.getPlayer().getName() + ".");
            event.setNewBalance(event.getOldBalance());
        }
    }

    @EventHandler
    private void playerChangeWorld(PlayerChangedWorldEvent event){
        plugin.getSALogger().log(Logger.LogType.DEBUG, "Worldchange event for " + event.getPlayer().getName() +
                " (New world: " + event.getPlayer().getWorld().getName() + ").");
        if(plugin.isAnniversaryWorld(event.getPlayer().getWorld().getName())){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Is anniversary world.");
            if(!plugin.getAnniversaryWorldID(event.getFrom().getName()).equals(plugin.getAnniversaryWorldID(event.getPlayer().getWorld().getName()))){
                Language language = LanguageHandler.getLanguage(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(StringList.TELEPORT_RETURN_GLOBAL_SPAWN.get(language));
            }
            addPlayer(event.getPlayer().getUniqueId());
        } else {
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Is not anniversary world.");
            removePlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event){
        playerJoin(event.getPlayer());
    }

    public void playerJoin(Player player){
        plugin.getSALogger().log(Logger.LogType.DEBUG, "Loading playerdata for " + player.getName());
        if(plugin.isAnniversaryWorld(player.getWorld().getName())){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Is anniversary world.");
            addPlayer(player.getUniqueId());
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.loadPlayer(player.getUniqueId()));
    }

    @EventHandler
    private void chunkGenerate(ChunkPopulateEvent event){
        if(plugin.isAnniversaryWorld(event.getWorld().getName())){
            plugin.getSALogger().log(Logger.LogType.FULL, "Generating new chunk in world: " + event.getWorld().getName() +
                    " at " + event.getChunk().getX() + ", " + event.getChunk().getZ());
        }
    }

    @EventHandler
    private void playerLeave(PlayerQuitEvent event){
        playerSet.remove(event.getPlayer().getUniqueId());
    }

    private void addPlayer(UUID uuid){
        playerSet.add(uuid);
    }

    private void removePlayer(UUID uuid){
        playerSet.remove(uuid);
    }
}
