package be4rjp.asyncobjectlib.object.tracker;

import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.ChunkUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkBaseObjectMap {

    private final Long2ObjectOpenHashMap<Set<AsyncObject>> asyncObjectMap = new Long2ObjectOpenHashMap<>();

    private final Set<AsyncObject> addObjects = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> removeObjects = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> removeObjectsIfNotMove = ConcurrentHashMap.newKeySet();

    private final Set<AsyncObject> trackedObject = new HashSet<>();

    private final Set<AsyncObject> hideObject = new HashSet<>();

    private final AsyncObjectPlayer asyncObjectPlayer;

    public ChunkBaseObjectMap(AsyncObjectPlayer asyncObjectPlayer){
        this.asyncObjectPlayer = asyncObjectPlayer;
    }

    public void addAsyncObject(AsyncObject asyncObject){this.addObjects.add(asyncObject);}

    public void removeAsyncObject(AsyncObject asyncObject){this.removeObjects.add(asyncObject);}

    public void removeAsyncObjectIfNotMove(AsyncObject asyncObject){this.removeObjectsIfNotMove.add(asyncObject);}


    private int tick = 0;

    public void doTick(){

        tick++;

        //AsyncObjectの追加
        for (AsyncObject asyncObject : addObjects) {
            Vector position = asyncObject.getPosition();
            long coord = ChunkUtil.toLongFromObjectPosition(position);

            asyncObjectMap.computeIfAbsent(coord, k -> new HashSet<>()).add(asyncObject);
        }
        addObjects.clear();

        //AsyncObjectの削除
        for (AsyncObject asyncObject : removeObjects) {
            trackedObject.remove(asyncObject);
            hideObject.remove(asyncObject);
            for (Set<AsyncObject> asyncObjects : asyncObjectMap.values()) {
                asyncObjects.remove(asyncObject);
            }
        }
        removeObjects.clear();
        for(AsyncObject asyncObject : removeObjectsIfNotMove){
            trackedObject.remove(asyncObject);
            hideObject.remove(asyncObject);

            long coord = ChunkUtil.toLongFromObjectPosition(asyncObject.getPosition());
            Set<AsyncObject> asyncObjects = asyncObjectMap.get(coord);
            if(asyncObjects != null) asyncObjects.remove(asyncObject);
        }


        if(tick % asyncObjectPlayer.getObjectDistanceCheckInterval() == 0) {
            Set<Long> rangeChunks = this.getRangeChunks();
            //スポーン処理
            for (long chunkPosition : rangeChunks) {
                Set<AsyncObject> asyncObjects = asyncObjectMap.get(chunkPosition);

                if(asyncObjects != null) {
                    for (AsyncObject asyncObject : asyncObjects) {
                        trackedObject.add(asyncObject);
                        hideObject.remove(asyncObject);
                        asyncObject.onSpawn();
                    }
                }
            }

            //デスポーン処理
            for (AsyncObject asyncObject : trackedObject) {
                long coord = ChunkUtil.toLongFromObjectPosition(asyncObject.getPosition());
                if (!rangeChunks.contains(coord)) {
                    if (!asyncObject.shouldDoTickAfterRemoved()) {
                        hideObject.add(asyncObject);
                    }
                    trackedObject.remove(asyncObject);
                    asyncObject.onRemove();
                }
            }
        }

        //AsyncObjectのtick実行
        trackedObject.forEach(AsyncObject::tick);
        hideObject.forEach(AsyncObject::tick);
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
}
