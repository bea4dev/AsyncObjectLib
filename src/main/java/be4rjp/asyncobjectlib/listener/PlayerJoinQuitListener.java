package be4rjp.asyncobjectlib.listener;

import be4rjp.asyncobjectlib.event.AsyncObjectPlayerCreateEvent;
import be4rjp.asyncobjectlib.event.AsyncObjectPlayerRemoveEvent;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        AsyncObjectPlayer asyncObjectPlayer = AsyncObjectPlayer.registerAsyncObjectPlayer(player);
        Bukkit.getPluginManager().callEvent(new AsyncObjectPlayerCreateEvent(asyncObjectPlayer));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        AsyncObjectPlayer asyncObjectPlayer = AsyncObjectPlayer.getAsyncObjectPlayer(player);
        AsyncObjectPlayerRemoveEvent removeEvent = new AsyncObjectPlayerRemoveEvent(asyncObjectPlayer);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if(!removeEvent.isCancelled()) AsyncObjectPlayer.removeAsyncObjectPlayer(player);
    }
}
