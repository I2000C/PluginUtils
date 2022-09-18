package me.i2000c.plugin_utils.listeners.inventories;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

//https://www.spigotmc.org/threads/detecting-custom-inventories-without-using-titles.517234/
public class InventoryListener implements Listener{
    private InventoryListener(){}
    
    public static void initialize(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
    }
    
    @EventHandler
    private static void onInventoryClick(InventoryClickEvent e){
        if(e.getView() == null || e.getView().getTitle() == null){
            return;
        }
        
        InventoryHolder topHolder = e.getView().getTopInventory().getHolder();
        if(topHolder != null && topHolder instanceof GUIFactory){
            GUIFactory holder = (GUIFactory) topHolder;
            holder.getFunction().accept(new CustomInventoryClickEvent(e));
        }
    }
}
