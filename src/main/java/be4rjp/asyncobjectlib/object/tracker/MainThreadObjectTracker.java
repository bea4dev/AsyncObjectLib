package be4rjp.asyncobjectlib.object.tracker;

import be4rjp.asyncobjectlib.AsyncObjectLib;
import org.bukkit.scheduler.BukkitRunnable;

public class MainThreadObjectTracker extends BukkitRunnable {

    private final ObjectTracker objectTracker;

    private final ChunkBaseObjectMap chunkBaseObjectMap;

    public MainThreadObjectTracker(ObjectTracker objectTracker){
        this.objectTracker = objectTracker;
        this.chunkBaseObjectMap = new ChunkBaseObjectMap(objectTracker.getAsyncObjectPlayer());
    }

    public ObjectTracker getObjectTracker() {return objectTracker;}

    public ChunkBaseObjectMap getChunkBaseObjectMap() {return chunkBaseObjectMap;}

    @Override
    public void run() {
        if(!objectTracker.getAsyncObjectPlayer().getPlayer().isOnline()){
            cancel();
            return;
        }
        chunkBaseObjectMap.doTick();
    }

    public void start(){
        this.runTaskTimer(AsyncObjectLib.getPlugin(),0, 1);
    }
}
