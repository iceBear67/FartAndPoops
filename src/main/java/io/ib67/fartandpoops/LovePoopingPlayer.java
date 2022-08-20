package io.ib67.fartandpoops;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LovePoopingPlayer implements FartPlayer {
    private long lastFartTime = System.currentTimeMillis();
    private final double poopTolerance;
    private final UUID uuid;

    public LovePoopingPlayer(double poopTolerance, UUID uuid) {
        this.poopTolerance = poopTolerance;
        this.uuid = uuid;
    }


    @Override
    public long getLastFartTime() {
        return lastFartTime;
    }

    @Override
    public void doFartOrPoop(boolean generateShitBlock) {
        var viewers = new ArrayList<Player>();
        viewers.addAll(getPlayer().getNearbyEntities(20, 10, 20).stream().filter(it -> it instanceof Player).map(it -> (Player) it).toList());
        viewers.add(getPlayer());
        if (generateShitBlock) {
            getPlayer().setSneaking(true);
            getSomeRandomLoc(getPlayer().getLocation().clone().add(0, -1, 0)).forEach(it -> spawnPoop(it, viewers));
        }
        lastFartTime = System.currentTimeMillis();
        var assDire = getPlayer().getFacing().getOppositeFace().getDirection();
        var aloc = getPlayer().getLocation().clone().add(0, 2, 0);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0xE67F25), 1.0F);
        getPlayer().spawnParticle(Particle.REDSTONE, aloc, 50, dustOptions);
        var loc = getPlayer().getLocation().clone().add(0, -1, 0).add(assDire);
        spawnPoop(loc, viewers);
    }

    private List<Location> getSomeRandomLoc(Location center) {
        var result = new ArrayList<Location>();
        for (int i = 0; i < 15; i++) {
            var loc = center.clone().add(5 - getRandomOffset(), 0, 5 - getRandomOffset());
            result.add(getSurfaceAt(loc));
        }
        return result;
    }

    private Location getSurfaceAt(Location loc) {
        int cnt = 0;
        Location t;
        for (t = loc.clone().subtract(0, 3, 0)
             ; !(t.getBlock().getType().isSolid()
                && !t.clone().add(0, 1, 0).getBlock().getType().isSolid())
                ; t.add(0, 1, 0)) {
            if (cnt++ > 6) return loc;
        }
        return t;
    }

    private static final int getRandomOffset() {
        return ThreadLocalRandom.current().nextInt(0, 10);
    }

    private void spawnPoop(Location location, List<Player> viewer) {
        viewer.forEach(it -> it.sendBlockChange(location, Bukkit.getServer().createBlockData(Material.WARPED_HYPHAE)));

    }

    @Override
    public double getPoopTolerance() {
        return poopTolerance;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void setLastFartTime(long time) {
        this.lastFartTime = time;
    }
}
