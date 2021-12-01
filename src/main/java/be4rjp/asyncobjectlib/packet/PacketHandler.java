package be4rjp.asyncobjectlib.packet;

import be4rjp.asyncobjectlib.AsyncObjectLib;
import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import be4rjp.asyncobjectlib.util.NMSUtil;
import io.netty.channel.*;
import org.bukkit.entity.Player;

import java.nio.channels.ClosedChannelException;

public class PacketHandler extends ChannelDuplexHandler{
    
    private final Player player;
    private final AsyncObjectPlayer asyncObjectPlayer;
    
    public PacketHandler(Player player, AsyncObjectPlayer asyncObjectPlayer){
        this.player = player;
        this.asyncObjectPlayer = asyncObjectPlayer;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
    
        String simpleName = packet.getClass().getSimpleName();
        if(simpleName.equalsIgnoreCase("PacketPlayInUseItem") ||
                simpleName.equalsIgnoreCase("PacketPlayInArmAnimation")){
            PacketManager.checkReadPacket(asyncObjectPlayer, packet, this, channelHandlerContext);
            return;
        }
        
        super.channelRead(channelHandlerContext, packet);
    }
    
    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, packet, channelPromise);
    }
    
    public void doRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception{
        try {
            Channel channel = NMSUtil.getChannel(player);
            
            ChannelHandler channelHandler = channel.pipeline().get(AsyncObjectLib.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.channelRead(channelHandlerContext, packet);
            }
        }catch (ClosedChannelException e){}
    }
    
    public void doWrite(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception{
        try {
            Channel channel = NMSUtil.getChannel(player);
            
            ChannelHandler channelHandler = channel.pipeline().get(AsyncObjectLib.getPlugin().getName() + "PacketInjector:" + player.getName());
            if(channelHandler != null && player.isOnline()) {
                super.write(channelHandlerContext, packet, channelPromise);
            }
        }catch (ClosedChannelException e){}
    }
}