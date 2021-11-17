package be4rjp.asyncobjectlib.event;

import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * AsyncObjectPlayerが作成された直後に呼び出されるイベント
 */
public class AsyncObjectPlayerCreateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {return HANDLERS;}

    public static HandlerList getHandlerList() {return HANDLERS;}


    private final AsyncObjectPlayer asyncObjectPlayer;

    public AsyncObjectPlayerCreateEvent(AsyncObjectPlayer asyncObjectPlayer){
        this.asyncObjectPlayer = asyncObjectPlayer;
    }

    public AsyncObjectPlayer getAsyncObjectPlayer() {return asyncObjectPlayer;}
}
