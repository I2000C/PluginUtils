package me.i2000c.plugin_utils;

import me.i2000c.plugin_utils.versions.MinecraftVersion;
import org.bukkit.Bukkit;

public class ReflectionUtils{
    public static Class<?> getNMSClass(String nmsPackage, String name) throws ClassNotFoundException{
        if(MinecraftVersion.getCurrentVersion().isNewNMS()){
            return Class.forName(nmsPackage + "." + name);
        }else{
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + "." + name);
        }        
    }
    
    public static Class<?> getCraftClass(String name) throws ClassNotFoundException{
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }
}
