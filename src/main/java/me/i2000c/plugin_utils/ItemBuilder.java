package me.i2000c.plugin_utils;

import com.cryptomorin.xseries.XMaterial;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.i2000c.plugin_utils.textures.Texture;
import me.i2000c.plugin_utils.textures.TextureException;
import me.i2000c.plugin_utils.textures.TextureManager;
import me.i2000c.plugin_utils.versions.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

@SuppressWarnings("deprecation")
public class ItemBuilder{
    private final ItemStack item;
    
    private ItemBuilder(XMaterial material){
        this.item = material.parseItem();
    }
    private ItemBuilder(ItemStack item){
        this.item = item;
    }
    
    private static Field itemMetaField;
    private ItemMeta getItemMeta(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            if(itemMetaField == null){
                itemMetaField = ItemStack.class.getDeclaredField("meta");
            }
            Object value = itemMetaField.get(item);
            if(value == null){
                return Bukkit.getItemFactory().getItemMeta(item.getType());
            }else{
                return (ItemMeta) value;
            }
        }catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex){
            return null;
        }
//</editor-fold>
    }
    private void setItemMeta(ItemMeta meta){
        //<editor-fold defaultstate="collapsed" desc="Code">
        item.setItemMeta(meta);
//</editor-fold>
    }
    
    public static ItemBuilder newItem(XMaterial material){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return new ItemBuilder(material);
//</editor-fold>
    }
    public static ItemBuilder newItem(String materialNameAndDurability){
        //<editor-fold defaultstate="collapsed" desc="Code">
        String[] splitted = materialNameAndDurability.split(":");
        String materialName = splitted[0];
        int materialID = -1;
        
        try{
            materialID = Integer.parseInt(materialName);
            Logger.warn("Using material IDs is deprecated and not recommended (materialID: " + materialID + ")");
        }catch(NumberFormatException ex){}
        
        Optional<XMaterial> optionalXMaterial;
        if(materialID == -1){
            optionalXMaterial = XMaterial.matchXMaterial(materialNameAndDurability);
        }else{
            if(splitted.length == 1){
                optionalXMaterial = XMaterial.matchXMaterial(materialID, (byte) 0);
            }else{
                optionalXMaterial = XMaterial.matchXMaterial(materialID, Byte.parseByte(splitted[1]));
            }
        }
        
        if(optionalXMaterial.isPresent()){
            return ItemBuilder.newItem(optionalXMaterial.get());
        }else{
            throw new IllegalArgumentException("Invalid ItemStack detected: " + materialNameAndDurability);
        }
//</editor-fold>
    }
    public static ItemBuilder fromItem(ItemStack item, boolean clone){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return new ItemBuilder(clone ? item.clone() : item);
//</editor-fold>
    }
    public static ItemBuilder fromItem(ItemStack item){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return ItemBuilder.fromItem(item, true);
//</editor-fold>
    }
    
    public ItemBuilder withMaterial(XMaterial material){
        //<editor-fold defaultstate="collapsed" desc="Code">
        material.setType(item);
        return this;
//</editor-fold>
    }
    public XMaterial getMaterial(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return XMaterial.matchXMaterial(item);
//</editor-fold>
    }
    
    public ItemBuilder withAmount(int amount){
        //<editor-fold defaultstate="collapsed" desc="Code">
        item.setAmount(amount);
        return this;
//</editor-fold>
    }
    public int getAmount(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return item.getAmount();
//</editor-fold>
    }
    
    public ItemBuilder withDurability(int durability){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(durability >= 0 && durability <= item.getType().getMaxDurability()){
            item.setDurability((short) durability);
        }
        
        return this;
//</editor-fold>
    }
    public short getDurability(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return item.getDurability();
//</editor-fold>
    }
    
    public ItemBuilder withDisplayName(String displayName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(displayName == null || displayName.isEmpty()){
            meta.setDisplayName(null);
        }else{
            meta.setDisplayName(Logger.color(displayName));
        }
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public String getDisplayName(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta.hasDisplayName()){
            return meta.getDisplayName();
        }else{
            return null;
        }
//</editor-fold>
    }
    public boolean hasDisplayName(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getItemMeta().hasDisplayName();
//</editor-fold>
    }
    
    public ItemBuilder addLoreLine(String loreLine){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        List<String> lore;
        if(meta.hasLore()){
            lore = meta.getLore();
        }else{
            lore = new ArrayList<>();
        }
        lore.add(Logger.color(loreLine));
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public ItemBuilder addLore(List<String> loreLines){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        List<String> lore;
        if(meta.hasLore()){
            lore = meta.getLore();
        }else{
            lore = new ArrayList<>();
        }
        lore.addAll(Logger.color(loreLines));
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public ItemBuilder withLore(String... lore){
        //<editor-fold defaultstate="collapsed" desc="Code">
        if(lore == null || lore.length == 0){
            return withLore((List<String>) null);
        }else{
            return withLore(Arrays.asList(lore));
        }
//</editor-fold>
    }
    public ItemBuilder withLore(List<String> lore){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(lore == null || lore.isEmpty()){
            meta.setLore(null);
        }else{
            meta.setLore(Logger.color(lore));
        }
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public List<String> getLore(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta.hasLore()){
            return meta.getLore();
        }else{
            return null;
        }
//</editor-fold>
    }
    public boolean hasLore(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getItemMeta().hasLore();
//</editor-fold>
    }
    
    public ItemBuilder addEnchantment(Enchantment enchantment, int level){
        //<editor-fold defaultstate="collapsed" desc="Code">
        Objects.requireNonNull(enchantment);
        
        item.addUnsafeEnchantment(enchantment, level);
        return this;
//</editor-fold>
    }
    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments){
        //<editor-fold defaultstate="collapsed" desc="Code">
        clearEnchantments();
        item.addUnsafeEnchantments(enchantments);
        return this;
//</editor-fold>
    }
    public ItemBuilder withEnchantments(List<String> enchantments){
        //<editor-fold defaultstate="collapsed" desc="Code">
        clearEnchantments();
        enchantments.forEach(enchant -> {
            String[] splitted = enchant.split(";");
            Enchantment enchantment = Enchantment.getByName(splitted[0]);
            Objects.requireNonNull(enchantment);
            
            int level = Integer.parseInt(splitted[1]);
            item.addUnsafeEnchantment(enchantment, level);
        });
        return this;
//</editor-fold>
    }
    public Map<Enchantment, Integer> getEnchantments(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return item.getEnchantments();
//</editor-fold>
    }
    public List<String> getEnchantmentsIntoStringList(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        List<String> enchantments = new ArrayList<>();
        item.getEnchantments().forEach((enchantment, level) -> {
            enchantments.add(enchantment.getName() + ";" + level);
        });
        return enchantments;
//</editor-fold>
    }
    public boolean hasEnchantments(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getItemMeta().hasEnchants();
//</editor-fold>
    }
    public ItemBuilder clearEnchantments(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        item.getEnchantments().forEach((enchantment, level) -> item.removeEnchantment(enchantment));
        return this;
//</editor-fold>
    }
    
    public ItemBuilder withOwner(String playerName){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof SkullMeta){
            ((SkullMeta) meta).setOwner(playerName);
            setItemMeta(meta);
        }
        return this;
//</editor-fold>
    }
    public ItemBuilder withOwner(Player player){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof SkullMeta){
            ((SkullMeta) meta).setOwningPlayer(player);
            setItemMeta(meta);
        }
        return this;
//</editor-fold>
    }
    public ItemBuilder withTextureID(String textureID){
        //<editor-fold defaultstate="collapsed" desc="Code">
        try{
            Texture texture = new Texture(textureID);
            return withTexture(texture);
        }catch(TextureException ex){
            Logger.err("An error occurred while setting texture of item:");
            Logger.err(ex);
            return this;
        }
//</editor-fold>
    }
    public ItemBuilder withTexture(Texture texture){
        //<editor-fold defaultstate="collapsed" desc="Code">
        TextureManager.setTexture(item, texture);
        return this;
//</editor-fold>
    }
    public Texture getTexture(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return TextureManager.getTexture(item);
//</editor-fold>
    }
    public boolean hasTexture(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getTexture() != null;
//</editor-fold>
    }
    
    public ItemBuilder addPotionEffect(PotionEffect potionEffect){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).addCustomEffect(potionEffect, true);
            setItemMeta(meta);
        }
        return this;
//</editor-fold>
    }
    public List<PotionEffect> getPotionEffects(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof PotionMeta){
            return ((PotionMeta) meta).getCustomEffects();
        }else{
            return null;
        }
//</editor-fold>
    }
    public ItemBuilder clearPotionEffects(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof PotionMeta){
            ((PotionMeta) meta).clearCustomEffects();
            setItemMeta(meta);
        }
        return this;
//</editor-fold>
    }
    
    public ItemBuilder withColor(Color color){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof LeatherArmorMeta){
            ((LeatherArmorMeta) meta).setColor(color);
        }else if(meta instanceof PotionMeta){
            if(MinecraftVersion.getCurrentVersion().isGreaterThanOrEqual(MinecraftVersion.v1_11)){
                ((PotionMeta) meta).setColor(color);
            }            
        }
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public Color getColor(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        if(meta instanceof LeatherArmorMeta){
            return ((LeatherArmorMeta) meta).getColor();
        }else if(meta instanceof PotionMeta){
            if(MinecraftVersion.getCurrentVersion().isGreaterThanOrEqual(MinecraftVersion.v1_11)){
                return ((PotionMeta) meta).getColor();
            }else{
                return null;
            }
        }else{
            return null;
        }
//</editor-fold>
    }
    public boolean hasColor(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getColor() != null;
//</editor-fold>
    }
    
    public ItemBuilder addItemFlags(ItemFlag... itemFlags){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(itemFlags);
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public ItemBuilder removeItemFlags(ItemFlag... itemFlags){
        //<editor-fold defaultstate="collapsed" desc="Code">
        ItemMeta meta = getItemMeta();
        meta.removeItemFlags(itemFlags);
        setItemMeta(meta);
        return this;
//</editor-fold>
    }
    public boolean hasItemFlag(ItemFlag itemFlag){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return getItemMeta().hasItemFlag(itemFlag);
//</editor-fold>
    }
    
    public ItemStack build(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return item;
//</editor-fold>
    }
    
    @Override
    public String toString(){
        //<editor-fold defaultstate="collapsed" desc="Code">
        return this.getMaterial().name();
//</editor-fold>
    }
}
