package be4rjp.asyncobjectlib.object.tracker;

import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import org.bukkit.World;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectTracker {

    private final AsyncObjectPlayer asyncObjectPlayer;

    private final World world;

    private MainThreadObjectTracker mainThreadObjectTracker;

    private AsyncThreadObjectTracker asyncThreadObjectTracker;

    private WorldThreadObjectTracker worldThreadObjectTracker;
    
    private final Set<AsyncObject> asyncObjects = ConcurrentHashMap.newKeySet();


    public ObjectTracker(AsyncObjectPlayer asyncObjectPlayer, World world){
        this.asyncObjectPlayer = asyncObjectPlayer;
        this.world = world;
    }

    public AsyncObjectPlayer getAsyncObjectPlayer() {return asyncObjectPlayer;}
    
    public World getWorld() {return world;}
    
    public Set<AsyncObject> getAsyncObjects() {return asyncObjects;}
    
    public synchronized void addAsyncObject(AsyncObject asyncObject){
        
        asyncObjects.add(asyncObject);
        
        switch (asyncObject.getTickType()){
            case ASYNC_THREAD:{
                if(asyncThreadObjectTracker == null){
                    asyncThreadObjectTracker = new AsyncThreadObjectTracker(this);
                    asyncThreadObjectTracker.start();
                }
                asyncThreadObjectTracker.getChunkBaseObjectMap().addAsyncObject(asyncObject);
                break;
            }

            case MAIN_THREAD:{
                if(mainThreadObjectTracker == null){
                    mainThreadObjectTracker = new MainThreadObjectTracker(this);
                    mainThreadObjectTracker.start();
                }
                mainThreadObjectTracker.getChunkBaseObjectMap().addAsyncObject(asyncObject);
                break;
            }

            case WORLD_THREAD:{
                if(worldThreadObjectTracker == null){
                    worldThreadObjectTracker = new WorldThreadObjectTracker(world, this);
                    worldThreadObjectTracker.start();
                }
                worldThreadObjectTracker.getChunkBaseObjectMap().addAsyncObject(asyncObject);
                break;
            }
        }
    }
    
    public AsyncThreadObjectTracker getAsyncThreadObjectTracker() {return asyncThreadObjectTracker;}
    
    public MainThreadObjectTracker getMainThreadObjectTracker() {return mainThreadObjectTracker;}
    
    public WorldThreadObjectTracker getWorldThreadObjectTracker() {return worldThreadObjectTracker;}
    
    public void setAsyncThreadObjectTracker(AsyncThreadObjectTracker asyncThreadObjectTracker) {this.asyncThreadObjectTracker = asyncThreadObjectTracker;}
    
    public void setMainThreadObjectTracker(MainThreadObjectTracker mainThreadObjectTracker) {this.mainThreadObjectTracker = mainThreadObjectTracker;}
    
    public void setWorldThreadObjectTracker(WorldThreadObjectTracker worldThreadObjectTracker) {this.worldThreadObjectTracker = worldThreadObjectTracker;}
}
