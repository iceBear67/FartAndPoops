package io.ib67.fartandpoops;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class FartPlayerFactory {
    private final NamespacedKey lastFartKey;

    public FartPlayerFactory(NamespacedKey lastFartKey) {

        this.lastFartKey = lastFartKey;
    }


    public FartPlayer createFartPlayer(Player player) {
        return new LovePoopingPlayer(
                ThreadLocalRandom.current().nextDouble(0.67,3),
                player.getUniqueId());
    }
}
