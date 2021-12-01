package be4rjp.asyncobjectlib.player;

import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.object.tracker.ObjectTracker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AsyncObjectPlayer {

    private static final Map<Player, AsyncObjectPlayer> asyncObjectPlayerMap = new HashMap<>();

    public static AsyncObjectPlayer getAsyncObjectPlayer(Player player){
        threadCheck();
        return asyncObjectPlayerMap.get(player);
    }

    public static AsyncObjectPlayer registerAsyncObjectPlayer(Player player){
        threadCheck();
        return asyncObjectPlayerMap.computeIfAbsent(player, AsyncObjectPlayer::new);
    }

    public static void removeAsyncObjectPlayer(Player player){asyncObjectPlayerMap.remove(player);}

    private static void threadCheck(){if(!Bukkit.isPrimaryThread()) throw new IllegalStateException("DO NOT CALL FROM ASYNC THREAD!");}



    private final Player player;

    private int viewDistance = 4;

    private int objectDistanceCheckInterval = 40;

    private final Map<World, ObjectTracker> objectTrackerMap = new ConcurrentHashMap<>();

    
    public AsyncObjectPlayer(Player player){this.player = player;}

    public Player getPlayer() {return player;}

    public int getViewDistance() {return viewDistance;}

    public void setViewDistance(int viewDistance) {this.viewDistance = viewDistance;}

    public int getObjectDistanceCheckInterval() {return objectDistanceCheckInterval;}

    public void setObjectDistanceCheckInterval(int objectDistanceCheckInterval) {this.objectDistanceCheckInterval = objectDistanceCheckInterval;}
    
    public ObjectTracker getObjectTracker(World world){return this.objectTrackerMap.get(world);}


    /**
     * AsyncObjectを追加します
     * @param asyncObject 追加するAsyncObject
     * @param world 追加するワールド
     */
    public void addAsyncObject(AsyncObject asyncObject, World world){
        objectTrackerMap.computeIfAbsent(world, k -> new ObjectTracker(this, k)).addAsyncObject(asyncObject);
    }
}
