package be4rjp.asyncobjectlib.packet;

import be4rjp.asyncobjectlib.object.AsyncObject;
import be4rjp.asyncobjectlib.object.tracker.ObjectTracker;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.RayTrace;
import be4rjp.asyncobjectlib.util.TaskHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.FluidCollisionMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

public class PacketManager {
    
    public static void checkReadPacket(AsyncObjectPlayer asyncObjectPlayer, Object packet, PacketHandler packetHandler, ChannelHandlerContext channelHandlerContext){
    
        Player player = asyncObjectPlayer.getPlayer();
        World world = player.getWorld();
        ObjectTracker objectTracker = asyncObjectPlayer.getObjectTracker(world);
        if(objectTracker == null){
            try {
                packetHandler.doRead(channelHandlerContext, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    
        Vector startPosition = player.getEyeLocation().toVector();
        Vector direction = player.getEyeLocation().getDirection();
        
        RayTrace rayTrace = new RayTrace(startPosition, direction);
        List<Vector> positions = rayTrace.traverse(4.0, 0.1);
        for(Vector position : positions){
            for(AsyncObject asyncObject : objectTracker.getAsyncObjects()){
                BoundingBox boundingBox = asyncObject.getBoundingBox();
                if(boundingBox == null) continue;
                
                if(boundingBox.contains(position)){
                    
                    double distance = 0.0;
                    try{
                        distance = startPosition.distance(position);
                    }catch (Exception e){e.printStackTrace();}
    
                    double finalDistance = distance;
                    TaskHandler.runWorldSync(world, () -> {
                        RayTraceResult rayTraceResult = world.rayTraceBlocks(startPosition.toLocation(world), direction, finalDistance, FluidCollisionMode.NEVER);
                        if(rayTraceResult == null){
                            switch (asyncObject.getTickType()){
                                case MAIN_THREAD:{
                                    TaskHandler.runSync(asyncObject::onClick);
                                    break;
                                }
                                case ASYNC_THREAD:{
                                    TaskHandler.runAsync(asyncObject::onClick);
                                    break;
                                }
                                case WORLD_THREAD:{
                                    TaskHandler.runWorldSync(world, asyncObject::onClick);
                                    break;
                                }
                            }
                        }else{
                            try {
                                packetHandler.doRead(channelHandlerContext, packet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                    });
                    
                    return;
                }
            }
        }
    
        try {
            packetHandler.doRead(channelHandlerContext, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
