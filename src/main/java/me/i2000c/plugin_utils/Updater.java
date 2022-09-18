package me.i2000c.plugin_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class Updater implements Listener{
    private static final int TIMEOUT = 1500;
    private static final String AUTHOR_PLACEHOLDER = "%author%";
    private static final String NAME_PLACEHOLDER = "%name%";
    private static final String VERSION_PLACEHOLDER = "%version%";
    private static final String BASE_URL_LINK = "https://github.com/"
            + AUTHOR_PLACEHOLDER + "/" + NAME_PLACEHOLDER
            + "/releases/download/v" + VERSION_PLACEHOLDER
            + "/" + NAME_PLACEHOLDER + ".jar";
    
    private final Plugin plugin;
    private final int resourceID;
    private final String downloadLink;
    
    private String latestversion;
    private boolean newUpdateDetected;
    private boolean autoUpdate;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public Updater(Plugin plugin, int resourceID, boolean autoUpdate, String downloadLink){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this.plugin = Objects.requireNonNull(plugin);
        this.resourceID = resourceID;
        this.autoUpdate = autoUpdate;
        if(autoUpdate){
            PluginDescriptionFile pdf = plugin.getDescription();
            String version = pdf.getVersion();
            String name = pdf.getName();
            String author;
            if(pdf.getAuthors().isEmpty()){
                author = null;
            }else{
                author = pdf.getAuthors().get(0);
            }
            
            String auxLink = Objects.requireNonNull(downloadLink);
            if(version != null){
                auxLink = auxLink.replace(VERSION_PLACEHOLDER, version);
            }
            if(name != null){
                auxLink = auxLink.replace(NAME_PLACEHOLDER, name);
            }
            if(author != null){
                auxLink = auxLink.replace(AUTHOR_PLACEHOLDER, author);
            }
            
            this.downloadLink = auxLink;
        }else{
            this.downloadLink = null;
        }
        
        this.latestversion = null;
        this.newUpdateDetected = false;
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
//</editor-fold>
    }
    public Updater(Plugin plugin, int resourceID, boolean autoUpdate){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this(plugin, resourceID, autoUpdate, BASE_URL_LINK);
//</editor-fold>
    }
    public Updater(Plugin plugin, int resourceID){
        //<editor-fold defaultstate="collapsed" desc="Code">
        this(plugin, resourceID, false);
//</editor-fold>
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public void checkUpdates(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Logger.log("&6Checking latest version...");
        try{
            String currentVersion = plugin.getDescription().getVersion();
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID).openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            latestversion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            if(!currentVersion.equals(latestversion)){
                int currentRevision = getRevision(currentVersion);
                int newRevision = getRevision(latestversion);
                if(newRevision > currentRevision){
                    Logger.log("&cThere is a new version available: &e(&7" + latestversion + "&e)");
                    Logger.log("&cYou can download it at: &fhttps://www.spigotmc.org/resources/" + resourceID + "/");
                    newUpdateDetected = true;
                }else{
                    Logger.log("&aYou are using the latest version");
                    newUpdateDetected = false;
                }
            }else{
                Logger.log("&aYou are using the latest version");
                newUpdateDetected = false;
            }
            connection.disconnect();
        }catch(Exception ex){
            Logger.err("&cAn error occurred while checking update:");
            Logger.err(ex);
            newUpdateDetected = false;
        }
        
        if(newUpdateDetected && autoUpdate){            
            Logger.log("&6Downloading update...");
            File updateFolder = Bukkit.getUpdateFolderFile();
            updateFolder.mkdir();
            File pluginFile = new File(updateFolder, plugin.getName() + ".jar");
            try{
                FileDownloader.downloadFile(downloadLink, pluginFile, true);
                Logger.log("&aUpdate downloaded");
                Logger.log("&aRestart the server to install it");
            }catch(IOException ex){
                Logger.err("&cAn error occurred while updating plugin:");
                Logger.err(ex);
                autoUpdate = false;
            }
        }
//</editor-fold>
    }
    
    private int getRevision(String version){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String[] splitted = version.split("_");
        return Integer.parseInt(splitted[splitted.length - 1]);
//</editor-fold>
    }
    
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Player player = event.getPlayer();
        if(!player.isOp()){
            return;
        }
        
        if(newUpdateDetected){
            Logger.sendMessage("&a========================================", player, false);
            Logger.sendMessage("", player);
            if(autoUpdate){
                Logger.sendMessage("&bA new update has been downloaded", player, false);
                Logger.sendMessage("&bRestart the server to install it", player, false);
            }else{
                Logger.sendMessage("&bThere is a new version available: &e(&7" + latestversion + "&e)", player, false);
                Logger.sendMessage("&bYou can download it at:&f https://www.spigotmc.org/resources/62644/", player, false);
            }
            Logger.sendMessage("&a========================================", player, false);
        }
//</editor-fold>
    }
}