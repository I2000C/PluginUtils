package me.i2000c.plugin_utils.versions;

public class InvalidVersionException extends Exception{
    public InvalidVersionException(double version){
        super("MinecraftVersion " + version + " doesn't exist");
    }
}
