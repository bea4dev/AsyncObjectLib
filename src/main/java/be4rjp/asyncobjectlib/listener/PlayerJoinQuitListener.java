package be4rjp.asyncobjectlib.listener;

import be4rjp.asyncobjectlib.AsyncObjectLib;
import be4rjp.asyncobjectlib.event.AsyncObjectPlayerCreateEvent;
import be4rjp.asyncobjectlib.event.AsyncObjectPlayerRemoveEvent;
import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.object.TickType;
import be4rjp.asyncobjectlib.packet.PacketHandler;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.NMSUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class PlayerJoinQuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        AsyncObjectPlayer asyncObjectPlayer = AsyncObjectPlayer.registerAsyncObjectPlayer(player);
        Bukkit.getPluginManager().callEvent(new AsyncObjectPlayerCreateEvent(asyncObjectPlayer));
        
        PacketHandler packetHandler = new PacketHandler(player, asyncObjectPlayer);
        try {
            ChannelPipeline pipeline = NMSUtil.getChannel(player).pipeline();
            pipeline.addBefore("packet_handler", AsyncObjectLib.getPlugin().getName() + "PacketInjector:" + player.getName(), packetHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        AsyncObjectPlayer asyncObjectPlayer = AsyncObjectPlayer.getAsyncObjectPlayer(player);
        AsyncObjectPlayerRemoveEvent removeEvent = new AsyncObjectPlayerRemoveEvent(asyncObjectPlayer);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if(!removeEvent.isCancelled()) AsyncObjectPlayer.removeAsyncObjectPlayer(player);
    
        try {
            Channel channel = NMSUtil.getChannel(player);
        
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(AsyncObjectLib.getPlugin().getName() + "PacketInjector:" + player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
