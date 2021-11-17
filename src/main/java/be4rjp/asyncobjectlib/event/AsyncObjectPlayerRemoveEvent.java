package be4rjp.asyncobjectlib.event;

import be4rjp.asyncobjectlib.player.AsyncObjectPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * AsyncObjectPlayerが削除される直前に呼び出されるイベント
 * キャンセル可能
 */
public class AsyncObjectPlayerRemoveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {return HANDLERS;}

    public static HandlerList getHandlerList() {return HANDLERS;}


    private final AsyncObjectPlayer asyncObjectPlayer;

    private boolean isCancelled = false;

    public AsyncObjectPlayerRemoveEvent(AsyncObjectPlayer asyncObjectPlayer){
        this.asyncObjectPlayer = asyncObjectPlayer;
    }

    public AsyncObjectPlayer getAsyncObjectPlayer() {return asyncObjectPlayer;}

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
