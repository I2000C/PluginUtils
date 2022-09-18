package me.i2000c.plugin_utils.listeners.inventories;

import javax.annotation.Nonnull;
import me.i2000c.plugin_utils.Logger;
import me.i2000c.plugin_utils.functions.InventoryFunction;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class GUIFactory implements InventoryHolder{    
    private final Inventory inventory;
    private final InventoryFunction function;
    
    @SuppressWarnings("LeakingThisInConstructor")
    private GUIFactory(String title, int size, InventoryFunction function){
        this.inventory = Bukkit.createInventory(this, size, Logger.color(title));
        this.function = function;
    }
    
    /**
     * Creates a new menu with
     * @param title The title of the menu
     * @param size The size of the menu (must be multiple of 9)
     * @param function The function to execute when a player clicks on the inventory
     * @return The created menu
     */
    public static Menu newMenu(String title, int size, InventoryFunction function){
        GUIFactory factory = new GUIFactory(title, size, function);
        return new Menu(factory.getInventory());
    }

    @Override
    public Inventory getInventory(){
        return this.inventory;
    }
    
    /**
     * Gets the function of this inventory.
     * @return The function to execute
     */
    @Nonnull
    public InventoryFunction getFunction(){
        if(this.function == null){
            return e -> {};
        }else{
            return this.function;
        }
    }
}
