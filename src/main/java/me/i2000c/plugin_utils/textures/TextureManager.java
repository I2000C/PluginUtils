package me.i2000c.plugin_utils.textures;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.i2000c.plugin_utils.ItemBuilder;
import me.i2000c.plugin_utils.Logger;
import me.i2000c.plugin_utils.ReflectionUtils;
import me.i2000c.plugin_utils.versions.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch", "deprecation", "unchecked"})
public class TextureManager{
    private static Constructor blockPostitionConstructor;
    private static Method getWorldHandle;
    private static Method getTileEntity;
    
    private static Method getGameProfile;
    private static Method setGameProfile;
    
    private static Field profileFieldItem;
    private static Field profileFieldBlock;
    
    private static Method update;
    
    static{
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            //https://www.spigotmc.org/threads/tutorial-reflection.147407/
            Class blockPositionClass = ReflectionUtils.getNMSClass("net.minecraft.core", "BlockPosition");
            blockPostitionConstructor = blockPositionClass.getConstructor(int.class, int.class, int.class);
            
            getWorldHandle = ReflectionUtils.getCraftClass("CraftWorld").getMethod("getHandle");
            
            Class worldServerClass = ReflectionUtils.getNMSClass("net.minecraft.world.level", "World");
            //Class worldServerClass = ReflectionUtils.getNMSClass("net.minecraft.server.level", "WorldServer");
            if(MinecraftVersion.getCurrentVersion().isGreaterThanOrEqual(MinecraftVersion.v1_18)){
                // In Minecraft 1.18 these classes' name have been changed
                // Go to "net.minecraft.world.level" and search "c_"
                getTileEntity = worldServerClass.getMethod("c_", blockPositionClass);
            }else{
                getTileEntity = worldServerClass.getMethod("getTileEntity", blockPositionClass);
            }
            
            try{
                getGameProfile = ReflectionUtils.getNMSClass("", "TileEntitySkull").getMethod("getGameProfile");
                setGameProfile = ReflectionUtils.getNMSClass("", "TileEntitySkull").getMethod("setGameProfile", GameProfile.class);
            }catch(ClassNotFoundException | NoSuchMethodException | SecurityException ex){
                getGameProfile = null;
                setGameProfile = null;
            }
            
            profileFieldItem = null;
            profileFieldBlock = null;
            
            update = null;
        }catch(Exception ex){
            Logger.err("An error ocurred while enabling TextureManager:");
            ex.printStackTrace();
        }
//</editor-fold>
    }
    
    public static Texture getTexture(ItemStack stack){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return null;
        }
        
        SkullMeta skMeta = (SkullMeta) meta;
        
        try{
            if(profileFieldItem == null){
                profileFieldItem = meta.getClass().getDeclaredField("profile");
                profileFieldItem.setAccessible(true);
            }
            GameProfile profile = (GameProfile) profileFieldItem.get(skMeta);
            return new Texture(profile);
        }catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex){
            return null;
        }
//</editor-fold>
    }
    public static boolean setTexture(ItemStack stack, Texture texture){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        try{
            SkullMeta sk = (SkullMeta) meta;            
            if(profileFieldItem == null){
                profileFieldItem = meta.getClass().getDeclaredField("profile");
                profileFieldItem.setAccessible(true);
            }
            profileFieldItem.set(meta, texture == null ? null : texture.getProfile());
            
            stack.setItemMeta(sk);
            if(MinecraftVersion.getCurrentVersion().isLegacyVersion()){
                stack.setDurability((short) 3);
            }
            
            return true;
        }catch (Exception ex){
            return false;
        }
//</editor-fold>
    }
    public static Texture getTexture(Block block){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(isSkull(block.getType())){
            try{
                Object tileEntitySkull;
                GameProfile profile;
                if(getGameProfile != null){
                    Object blockPosition = blockPostitionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
                    Object world = getWorldHandle.invoke(block.getWorld());
                    tileEntitySkull = getTileEntity.invoke(world, blockPosition);
                    
                    profile = (GameProfile) getGameProfile.invoke(tileEntitySkull);
                }else{
                    tileEntitySkull = block.getState();
                    if(profileFieldBlock == null){
                        try{
                            profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("profile");
                        }catch(NoSuchFieldException ex){
                            profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("gameProfile");
                        }
                        profileFieldBlock.setAccessible(true);
                    }
                    profile = (GameProfile) profileFieldBlock.get(tileEntitySkull);
                }
                return new Texture(profile);
            }catch(Exception ex){
                //block is not a head block
                return null;
            }
        }else{
            return null;
        }
//</editor-fold>
    }
    public static boolean setTexture(Block block, Texture texture, boolean force){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(!isSkull(block.getType())){
            return false;
        }
        
        try{            
            if(texture != null && force && !isSkull(block.getType())){
                block.setType(getBlockSkullMaterial());
                if(MinecraftVersion.getCurrentVersion().isLegacyVersion()){
                    block.setData((byte) 1);
                }
            }
            
            Object tileEntitySkull;
            if(setGameProfile != null){
                Object blockPosition = blockPostitionConstructor.newInstance(block.getX(), block.getY(), block.getZ());
                Object world = getWorldHandle.invoke(block.getWorld());
                tileEntitySkull = getTileEntity.invoke(world, blockPosition);
                
                setGameProfile.invoke(tileEntitySkull, texture == null ? null : texture.getProfile());
            }else{
                tileEntitySkull = block.getState();
                if(profileFieldBlock == null){
                    try{
                        profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("profile");
                    }catch(NoSuchFieldException ex){
                        profileFieldBlock = tileEntitySkull.getClass().getDeclaredField("gameProfile");
                    }
                    profileFieldBlock.setAccessible(true);
                }
                profileFieldBlock.set(tileEntitySkull, texture == null ? null : texture.getProfile());
                
                if(update == null){
                    update = tileEntitySkull.getClass().getMethod("update", boolean.class);
                }                    
                update.invoke(tileEntitySkull, true);
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
//</editor-fold>
    }
    
    public static boolean setOwningPlayer(ItemStack stack, Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = stack.getItemMeta();
        if(!(meta instanceof SkullMeta)){
            return false;
        }
        
        SkullMeta sk = (SkullMeta) meta;
        sk.setOwner(player.getName());
        stack.setItemMeta(sk);
        
        return true;
//</editor-fold>
    }
    
    public static ItemStack getTexturedHead(Texture texture){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return ItemBuilder.newItem(XMaterial.PLAYER_HEAD)
                .withDisplayName("&bCustom head")
                .withTexture(texture)
                .build();
//</editor-fold>
    }
    public static boolean isSkull(Material m){
        //<editor-fold defaultstate="collapsed" desc="Code">
        XMaterial material = XMaterial.matchXMaterial(m);
        switch(material){
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
                return true;
            default:
                return false;
        }
//</editor-fold>
    }
    public static Material getBlockSkullMaterial(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(MinecraftVersion.getCurrentVersion().isLegacyVersion()){
            return Material.SKULL;
        }else{
            return XMaterial.PLAYER_HEAD.parseMaterial();
        }
//</editor-fold>
    }
}
