package me.i2000c.plugin_utils.functions;

import java.util.function.BiConsumer;
import org.bukkit.entity.Player;

public interface EditorNextFunction<T> extends BiConsumer<Player, T>{}
