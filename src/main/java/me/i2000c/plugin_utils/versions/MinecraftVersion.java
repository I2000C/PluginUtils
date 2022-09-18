package me.i2000c.plugin_utils.versions;

import org.bukkit.Bukkit;

public enum MinecraftVersion{
    v1_8(true, false),
    v1_9(true, false),
    v1_10(true, false),
    v1_11(true, false),
    v1_12(true, false),
    v1_13(false, false),
    v1_14(false, false),
    v1_15(false, false),
    v1_16(false, false),
    v1_17(false, true),
    v1_18(false, true),
    v1_19(false, true);
    
    private static final MinecraftVersion[] VALUES = values();    
    private static MinecraftVersion currentVersion;
    private final boolean isLegacyVersion;    
    private final boolean isNewNMS;
    
    private MinecraftVersion(boolean isLegacyVersion, boolean isNewNMS){
        this.isLegacyVersion = isLegacyVersion;
        this.isNewNMS = isNewNMS;
    }
    
    public boolean isLegacyVersion(){
        return isLegacyVersion;
    }    
    public boolean isNewNMS(){
        return isNewNMS;
    }
    
    public boolean isGreaterThan(MinecraftVersion version){
        return this.compareTo(version) > 0;
    }
    public boolean isGreaterThanOrEqual(MinecraftVersion version){
        return this.compareTo(version) >= 0;
    }
    public boolean isLessThan(MinecraftVersion version){
        return this.compareTo(version) < 0;
    }
    public boolean isLessThanOrEqual(MinecraftVersion version){
        return this.compareTo(version) <= 0;
    }
    
    public double toDouble(){
        return Double.parseDouble(this.name().replace("v", "").replace('_', '.'));
    }
    
    @Override
    public String toString(){
        return this.name().replace("v", "").replace('_', '.');
    }
    
    
    
    public static MinecraftVersion fromDouble(double version) throws InvalidVersionException{
        String versionName = String.valueOf(version);
        versionName = versionName.replace('.', '_');
        try{
            return valueOf("v" + versionName);
        }catch(IllegalArgumentException ex){
            throw new InvalidVersionException(version);
        }        
    }
    
    public static void loadCurrentVersion() throws UnsupportedVersionException{
        String version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);
        version = version.substring(0, version.lastIndexOf('_'));
        try{
            currentVersion = valueOf(version);
        }catch(IllegalArgumentException ex){
            currentVersion = null;
            throw new UnsupportedVersionException();
        }
    }
    
    public static MinecraftVersion getCurrentVersion(){
        return currentVersion;
    }
    
    public static MinecraftVersion getOldestVersion(){
        return VALUES[0];
    }
    public static MinecraftVersion getLatestVersion(){
        return VALUES[VALUES.length - 1];
    }
}
