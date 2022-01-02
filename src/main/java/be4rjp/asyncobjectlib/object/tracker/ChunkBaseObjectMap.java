package be4rjp.asyncobjectlib.object.tracker;

import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.ChunkUtil;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkBaseObjectMap {

    private final Long2ObjectOpenHashMap<Set<AsyncObject>> asyncObjectMap = new Long2ObjectOpenHashMap<>();

    private final Set<AsyncObject> addObjects = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> removeObjects = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> removeObjectsIfNotMove = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> trackedObjects = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> hideObjects = new HashSet<>();

    private final AsyncObjectPlayer asyncObjectPlayer;
    
    private final ObjectTracker objectTracker;

    public ChunkBaseObjectMap(AsyncObjectPlayer asyncObjectPlayer, ObjectTracker objectTracker){
        this.asyncObjectPlayer = asyncObjectPlayer;
        this.objectTracker = objectTracker;
    }

    public void addAsyncObject(AsyncObject asyncObject){this.addObjects.add(asyncObject);}

    public void removeAsyncObject(AsyncObject asyncObject){this.removeObjects.add(asyncObject);}

    public void removeAsyncObjectIfNotMove(AsyncObject asyncObject){this.removeObjectsIfNotMove.add(asyncObject);}


    private int tick = 0;

    public void doTick(){
        
        if(asyncObjectPlayer.getPlayer().getWorld() != objectTracker.getWorld()) return;

        tick++;

        //AsyncObjectの追加
        for (AsyncObject asyncObject : addObjects) {
            Vector position = asyncObject.getPosition();
            long coordinate = ChunkUtil.toLongFromObjectPosition(position);

            asyncObjectMap.computeIfAbsent(coordinate, k -> new HashSet<>()).add(asyncObject);
        }
        addObjects.clear();

        //AsyncObjectの削除
        for (AsyncObject asyncObject : removeObjects) {
            trackedObjects.remove(asyncObject);
            hideObjects.remove(asyncObject);
            for (Set<AsyncObject> asyncObjects : asyncObjectMap.values()) {
                asyncObjects.remove(asyncObject);
            }
        }
        removeObjects.clear();
        for(AsyncObject asyncObject : removeObjectsIfNotMove){
            trackedObjects.remove(asyncObject);
            hideObjects.remove(asyncObject);

            long coordinate = ChunkUtil.toLongFromObjectPosition(asyncObject.getPosition());
            Set<AsyncObject> asyncObjects = asyncObjectMap.get(coordinate);
            if(asyncObjects != null) asyncObjects.remove(asyncObject);
        }


        if(tick % asyncObjectPlayer.getObjectDistanceCheckInterval() == 0) {
            Set<Long> rangeChunks = this.getRangeChunks();
            //スポーン処理
            for (long chunkPosition : rangeChunks) {
                Set<AsyncObject> asyncObjects = asyncObjectMap.get(chunkPosition);

                if(asyncObjects != null) {
                    for (AsyncObject asyncObject : asyncObjects) {
                        if(!trackedObjects.contains(asyncObject)) {
                            trackedObjects.add(asyncObject);
                            hideObjects.remove(asyncObject);
                            asyncObject.onSpawn();
                        }
                    }
                }
            }

            //デスポーン処理
            for (AsyncObject asyncObject : trackedObjects) {
                long coordinate = ChunkUtil.toLongFromObjectPosition(asyncObject.getPosition());
                if (!rangeChunks.contains(coordinate)) {
                    if (asyncObject.shouldDoTickAfterRemoved()) {
                        hideObjects.add(asyncObject);
                    }
                    trackedObjects.remove(asyncObject);
                    asyncObject.onRemove();
                }
            }
        }

        //AsyncObjectのtick実行
        trackedObjects.forEach(AsyncObject::tick);
        hideObjects.forEach(AsyncObject::tick);
        
        //isRemove()がtrueになっているObjectを完全に削除
        trackedObjects.removeIf(AsyncObject::isDead);
        hideObjects.removeIf(AsyncObject::isDead);
        
        for(Set<AsyncObject> asyncObjects : asyncObjectMap.values()){
            asyncObjects.removeIf(AsyncObject::isDead);
        }
    }

    private Set<Long> getRangeChunks(){
        int range = asyncObjectPlayer.getViewDistance();
        Location location = asyncObjectPlayer.getPlayer().getLocation();
        Set<Long> chunkPositions = new HashSet<>();
        for(int x = -range; x < range; x++){
            for(int z = -range; z < range; z++){
                chunkPositions.add(ChunkUtil.toLongChunkPosition((location.getBlockX() >> 4) + x, (location.getBlockZ() >> 4) + z));
            }
        }

        return chunkPositions;
    }
    
    
    public void unloadAll(){
        trackedObjects.forEach(AsyncObject::onRemove);
        trackedObjects.clear();
        
        hideObjects.forEach(AsyncObject::onRemove);
        hideObjects.clear();
    }
}
