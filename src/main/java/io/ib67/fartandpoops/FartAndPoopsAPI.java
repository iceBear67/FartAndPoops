package io.ib67.fartandpoops;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public interface FartAndPoopsAPI {
    FartPlayer getFartPlayer(Player player);

    Map<UUID, BukkitTask> getFartingPlayers();
    static FartAndPoopsAPI getInst(){
        return FartAndPoops.getPlugin(FartAndPoops.class);
    }
}
