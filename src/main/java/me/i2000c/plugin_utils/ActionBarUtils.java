package me.i2000c.plugin_utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.plugin_utils.versions.MinecraftVersion;
import org.bukkit.entity.Player;

public class ActionBarUtils{
    private static Constructor chatComponentTextConstructor;
    private static Constructor packetPlayOutChatConstructor;    
    private static Method getHandle;
    private static Field playerConnection;    
    private static Method sendPacket;
    private static boolean initialized = false;
    
    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace", "unchecked"})
    public static void sendMessage(String message, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        message = Logger.color(message);
        if(MinecraftVersion.getCurrentVersion() == MinecraftVersion.v1_8){
            try{
                if(!initialized){
                    Class chatBaseComponentClass = ReflectionUtils.getNMSClass(null, "IChatBaseComponent");
                    Class chatComponentText = ReflectionUtils.getNMSClass(null, "ChatComponentText");
                    chatComponentTextConstructor = chatComponentText.getConstructor(String.class);
                    
                    Class packetPlayOutChatClass = ReflectionUtils.getNMSClass(null, "PacketPlayOutChat");
                    packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(chatBaseComponentClass, byte.class);
                    
                    getHandle = ReflectionUtils.getCraftClass("entity.CraftPlayer").getMethod("getHandle");
                    playerConnection = ReflectionUtils.getNMSClass(null, "EntityPlayer").getField("playerConnection");
                    
                    Class packetClass = ReflectionUtils.getNMSClass(null, "Packet");
                    sendPacket = ReflectionUtils.getNMSClass(null, "PlayerConnection").getMethod("sendPacket", packetClass);
                    
                    initialized = true;
                }
                
                Object component = chatComponentTextConstructor.newInstance(message);
                Object packet = packetPlayOutChatConstructor.newInstance(component, (byte) 2);
                Object nmsPlayer = getHandle.invoke(player);
                sendPacket.invoke(playerConnection.get(nmsPlayer), packet);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            //https://www.spigotmc.org/threads/tutorial-send-actionbar-messages-without-nms.257845/
            net.md_5.bungee.api.ChatMessageType type = net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
            net.md_5.bungee.api.chat.BaseComponent[] component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message);
            player.spigot().sendMessage(type, component);
        }
//</editor-fold>
    }
}
