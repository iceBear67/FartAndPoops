package io.ib67.fartandpoops;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FartAndPoops extends JavaPlugin implements FartAndPoopsAPI, Listener {
    private final Map<UUID, FartPlayer> players = new HashMap<>();
    private final NamespacedKey lastFartKey = new NamespacedKey(this, "last_fart");
    private final FartPlayerFactory factory = new FartPlayerFactory(lastFartKey);

    private final Map<UUID, BukkitTask> poopingPlayers = new HashMap<>();
    private Metrics metrics;

    @Override
    public void onEnable() {
        // Plugin startup logic
        new Metrics(this,16205);
        getServer().getPluginManager().registerEvents(this, this);
        new FartReminderTask().runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains("我想拉屎")) {
            var f = getFartPlayer(event.getPlayer());
            f.setLastFartTime((long) (System.currentTimeMillis() - (1000 * 1000) * f.getPoopTolerance()));
        }
    }

    @Override
    public FartPlayer getFartPlayer(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), it -> factory.createFartPlayer(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var fartPlayer = players.get(event.getPlayer().getUniqueId());
        // save player
        var pdc = event.getPlayer().getPersistentDataContainer();
        //pdc.set(recentFoodKey, RecentlyFoodTag.INSTANCE, fartPlayer.getRecentFoods());
        pdc.set(lastFartKey, PersistentDataType.LONG, fartPlayer.getLastFartTime());
        poopingPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        getFartPlayer(event.getPlayer()); // initialize
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(poopingPlayers.containsKey(event.getPlayer().getUniqueId())){
            var block = event.getTo().getBlock();
            var lowerBlock = block.getLocation().clone().add(0,-0.2F,0).getBlock();
            if(!isWaterLogged(block) && !isWaterLogged(lowerBlock)){
                poopingPlayers.remove(event.getPlayer().getUniqueId()).cancel();;
                event.getPlayer().sendTitle(ChatColor.RED + "你跳了出来", "屎被你憋了回去", 10, 20, 10);
            }
        }
    }
    @EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            var fartPlayer = getFartPlayer(event.getPlayer());
            var delta = System.currentTimeMillis() - fartPlayer.getLastFartTime();
            var range = (1000 * 1000) * fartPlayer.getPoopTolerance(); // chance * 1000 seconds
            if (delta - range >= 0) {
                // can fart
                var block = event.getPlayer().getLocation().getBlock();
                var lowerBlock = block.getLocation().clone().add(0,-0.2F,0).getBlock();
                if (!isWaterLogged(block) && !isWaterLogged(lowerBlock)) {
                    event.getPlayer().sendMessage(ChatColor.RED+"你只能在含水的方块或水中排泄！");
                    return;
                }
                var task = Bukkit.getScheduler().runTaskLater(this, () -> {
                    fartPlayer.doFartOrPoop(false);
                    event.getPlayer().sendMessage(ChatColor.GRAY + "噢......你拉了出来，感觉舒服多了。");
                    poopingPlayers.remove(event.getPlayer().getUniqueId());
                }, 5 * 20L);
                poopingPlayers.put(event.getPlayer().getUniqueId(), task);
                event.getPlayer().sendTitle(ChatColor.GREEN + "你正在拉屎", "请坚持一小会", 10, 20, 10);
            }
        } else if (poopingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendTitle(ChatColor.RED + "你站了起来", "屎被你憋了回去", 10, 20, 10);
            poopingPlayers.remove(event.getPlayer().getUniqueId()).cancel();
        }
    }
    private static final boolean isWaterLogged(Block block){
        return block.getType() == Material.WATER || (block.getBlockData() instanceof Waterlogged wBlock && wBlock.isWaterlogged());
    }
    @Override
    public Map<UUID, BukkitTask> getFartingPlayers() {
        return poopingPlayers;
    }
    // 腹泻套餐
    public static final Map<Material, Integer> POOP_FOODS = Map.of(
            Material.PUFFERFISH, 5,
            Material.SUSPICIOUS_STEW, 2 * 60 * 1000,
            Material.POISONOUS_POTATO, 5 * 1000,
            Material.MUTTON, 4 * 60 * 1000,
            Material.PORKCHOP, 3 * 60 * 1000,
            Material.CHICKEN, 4 * 60 * 1000,
            Material.RABBIT, 4 * 60 * 1000,
            Material.COD, 20 * 60 * 1000,
            Material.ROTTEN_FLESH, 30 * 1000,
            Material.SPIDER_EYE, 0
    );
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        //getFartPlayer(event.getPlayer()).getRecentFoods().addFood(event.getItem().getType());
        // check food type
        var bouns = POOP_FOODS.get(event.getItem().getType());
        if(bouns != null){
            var f=getFartPlayer(event.getPlayer());
            f.setLastFartTime((long) (System.currentTimeMillis() - (1000 * 1000) * f.getPoopTolerance()) + bouns);
        }
    }
}
