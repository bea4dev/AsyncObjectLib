package be4rjp.asyncobjectlib.listener;

import be4rjp.asyncobjectlib.event.AsyncObjectPlayerCreateEvent;
import be4rjp.asyncobjectlib.event.AsyncObjectPlayerRemoveEvent;
import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.object.tracker.ObjectTracker;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.RayTrace;
import be4rjp.asyncobjectlib.util.TaskHandler;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        var asyncObjectPlayer = AsyncObjectPlayer.getAsyncObjectPlayer(event.getPlayer());
        Player player = asyncObjectPlayer.getPlayer();
        World world = player.getWorld();
        ObjectTracker objectTracker = asyncObjectPlayer.getObjectTracker(world);

        if (objectTracker == null) {
            return;
        }

        Vector startPosition = player.getEyeLocation().toVector();
        Vector direction = player.getEyeLocation().getDirection();

        RayTrace rayTrace = new RayTrace(startPosition, direction);
        List<Vector> positions = rayTrace.traverse(4.0, 0.1);
        for (Vector position : positions) {
            for (AsyncObject asyncObject : objectTracker.getAsyncObjects()) {
                BoundingBox boundingBox = asyncObject.getBoundingBox();
                if (boundingBox == null) continue;

                if (boundingBox.contains(position)) {

                    double distance = 0.0;
                    try {
                        distance = startPosition.distance(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    double finalDistance = distance;
                    RayTraceResult rayTraceResult = world.rayTraceBlocks(startPosition.toLocation(world), direction, finalDistance, FluidCollisionMode.NEVER);
                    if (rayTraceResult == null) {
                        switch (asyncObject.getTickType()) {
                            case MAIN_THREAD: {
                                TaskHandler.runSync(asyncObject::onClick);
                                break;
                            }
                            case ASYNC_THREAD: {
                                TaskHandler.runAsync(asyncObject::onClick);
                                break;
                            }
                            case WORLD_THREAD: {
                                TaskHandler.runWorldSync(world, asyncObject::onClick);
                                break;
                            }
                        }
                    }

                    event.setCancelled(true);
                }
            }
        }
    }

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
