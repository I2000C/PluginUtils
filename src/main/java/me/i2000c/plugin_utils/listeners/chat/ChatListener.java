package me.i2000c.plugin_utils.listeners.chat;

import java.util.HashMap;
import java.util.Map;
import me.i2000c.plugin_utils.Task;
import me.i2000c.plugin_utils.functions.ChatFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatListener implements Listener{
    private ChatListener(){}
    
    public static void initialize(Plugin plugin){
        Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);
    }
    
    private static final Map<Player, ChatFunction> PLAYERS = new HashMap<>();
    
    public static void registerPlayer(Player player, ChatFunction function){
        registerPlayer(player, function, true);
    }
    
    public static void registerPlayer(Player player, ChatFunction function, boolean autoRemove){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(autoRemove){
            PLAYERS.put(player, (message) -> {
                PLAYERS.remove(player);
                function.accept(message);
            });
        }else{
            PLAYERS.put(player, function);
        }
//</editor-fold>
    }
    
    public static void removePlayer(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        PLAYERS.remove(player);
//</editor-fold>
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private static void onPlayerChat(AsyncPlayerChatEvent e){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(PLAYERS.isEmpty()){
            return;
        }
        
        ChatFunction function = PLAYERS.get(e.getPlayer());
        if(function != null){
            e.setCancelled(true);
            Task.runTask(() -> function.accept(e.getMessage()));
        }
//</editor-fold>
    }
}
