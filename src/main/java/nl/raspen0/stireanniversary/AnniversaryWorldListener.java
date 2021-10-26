package nl.raspen0.stireanniversary;

import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        System.out.println("Storage click.");
        if(event.getClickedInventory() == null || event.getCurrentItem() == null){
            return;
        }

        if(!playerSet.contains(event.getWhoClicked().getUniqueId())){
            return;
        }

        if(event.getWhoClicked().hasPermission("stireanniversary.bypass.chest")){
            return;
        }

        if(event.getClickedInventory().getType().equals(InventoryType.PLAYER)){
            return;
        }

        switch (event.getCurrentItem().getType()){
            case WRITTEN_BOOK, WRITABLE_BOOK, FILLED_MAP -> event.getWhoClicked().setItemOnCursor(event.getCurrentItem().clone());
            default -> event.setCancelled(true);
        }
    }

    @EventHandler
    private void storageDrag(InventoryDragEvent event){
        System.out.println("Storage drag.");
        if(!playerSet.contains(event.getWhoClicked().getUniqueId())){
            return;
        }

        if(event.getWhoClicked().hasPermission("stireanniversary.bypass.chest")){
            return;
        }

        if(!event.getInventory().getType().equals(InventoryType.PLAYER)){
            event.setCancelled(true);
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
            addPlayer(event.getPlayer().getUniqueId());
        } else {
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Is not anniversary world.");
            removePlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event){
        plugin.getSALogger().log(Logger.LogType.DEBUG, "Loading playerdata for " + event.getPlayer().getName());
        if(plugin.isAnniversaryWorld(event.getPlayer().getWorld().getName())){
            plugin.getSALogger().log(Logger.LogType.DEBUG, "Is anniversary world.");
            addPlayer(event.getPlayer().getUniqueId());
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.loadPlayer(event.getPlayer().getUniqueId()));
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
