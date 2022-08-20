package io.ib67.fartandpoops;
import org.bukkit.entity.Player;

public interface FartPlayer {
    void doFartOrPoop(boolean generateShitBlock);
    long getLastFartTime();
    double getPoopTolerance();
    Player getPlayer();

    void setLastFartTime(long time);

}
