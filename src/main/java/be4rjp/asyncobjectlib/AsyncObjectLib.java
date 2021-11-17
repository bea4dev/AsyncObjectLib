package be4rjp.asyncobjectlib;

import be4rjp.asyncobjectlib.listener.PlayerJoinQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsyncObjectLib extends JavaPlugin {

    private static AsyncObjectLib plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinQuitListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AsyncObjectLib getPlugin() {return plugin;}
}
