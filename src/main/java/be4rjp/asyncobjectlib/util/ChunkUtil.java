package be4rjp.asyncobjectlib.util;

import org.bukkit.util.Vector;

public class ChunkUtil {

    public static long toLongChunkPosition(int x, int z){
        return  ((long)x << 32) | (z & 0xFFFFFFFFL);
    }

    public static long toLongFromObjectPosition(Vector position){
        return toLongChunkPosition(position.getBlockX() >> 4, position.getBlockZ() >> 4);
    }

}
