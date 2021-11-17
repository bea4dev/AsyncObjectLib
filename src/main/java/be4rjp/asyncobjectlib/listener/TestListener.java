package be4rjp.asyncobjectlib.listener;

import be4rjp.asyncobjectlib.event.AsyncObjectPlayerCreateEvent;
import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.object.TickType;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class TestListener implements Listener {
    
    @EventHandler
    public void onCreate(AsyncObjectPlayerCreateEvent event){
        AsyncObjectPlayer asyncObjectPlayer = event.getAsyncObjectPlayer();
        Player player = asyncObjectPlayer.getPlayer();
        
        Vector position = player.getLocation().toVector();
    
        AsyncObject asyncObject = new AsyncObject() {
            @Override
            public void onSpawn() {
                player.sendMessage("SPAWN!");
            }
    
            @Override
            public void onRemove() {
                player.sendMessage("REMOVE!");
            }
    
            @Override
            public void tick() {
                player.sendMessage("TICK!");
            }
    
            @Override
            public TickType getTickType() {
                return TickType.ASYNC_THREAD;
            }
    
            @Override
            public Vector getPosition() {
                return position;
            }
    
            @Override
            public boolean shouldDoTickAfterRemoved() {
                return false;
            }
        };
        
        asyncObjectPlayer.addAsyncObject(asyncObject, player.getWorld());
    }
    
}
