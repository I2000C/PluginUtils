package me.i2000c.plugin_utils.listeners.inventories;

import java.util.function.Consumer;

public class GUIManager{
    private static Consumer<Menu> onMenuSetted;
    public static void setOnMenuClearedSetted(Consumer<Menu> onMenuSetted){
        GUIManager.onMenuSetted = onMenuSetted;
    }
    
    private static Menu currentMenu = null;
    
    public static void setCurrentMenu(Menu menu){
        currentMenu = menu;
        onMenuSetted.accept(menu);
    }
    
    public static void clearCurrentMenu(){
        setCurrentMenu(null);
    }
    
    public static Menu getCurrentMenu(){
        return currentMenu;
    }
}
