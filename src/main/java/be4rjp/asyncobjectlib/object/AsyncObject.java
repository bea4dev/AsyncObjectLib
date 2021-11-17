package be4rjp.asyncobjectlib.object;

import org.bukkit.util.Vector;

public interface AsyncObject {

    /**
     * スポーンするときに呼び出される関数
     */
    void onSpawn();

    /**
     * デスポーンするときに呼び出される関数
     */
    void onRemove();

    /**
     * 毎tick呼び出される関数
     */
    void tick();

    /**
     * tick実行するときのtype
     * @return TickType
     */
    TickType getTickType();

    /**
     * @return 現在の座標
     */
    Vector getPosition();

    /**
     * @return 削除された後もtick実行を行うかどうか
     */
    boolean shouldDoTickAfterRemoved();

}
