package me.i2000c.plugin_utils.functions;

import java.util.function.Consumer;
import me.i2000c.plugin_utils.listeners.inventories.CustomInventoryClickEvent;

@FunctionalInterface
public interface InventoryFunction extends Consumer<CustomInventoryClickEvent>{}
