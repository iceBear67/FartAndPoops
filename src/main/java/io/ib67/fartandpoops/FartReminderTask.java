package io.ib67.fartandpoops;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public class FartReminderTask extends BukkitRunnable {
    @Override
    public void run() {
        var api = FartAndPoopsAPI.getInst();
        Bukkit.getOnlinePlayers().stream().map(api::getFartPlayer).forEach(this::remind);
    }

    private void remind(FartPlayer fartPlayer) {
        var delta = System.currentTimeMillis() - fartPlayer.getLastFartTime();
        var range = (1000 * 1000) * fartPlayer.getPoopTolerance(); // chance * 1000 seconds
        if (delta - range >= 0) { // delta > range
            fartPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "你感到胃里排江倒海... " + ChatColor.GRAY + "(快按下 Shift 排泄！)"));
            fartPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 2 * 10, 2, true));
            var hasTask = FartAndPoopsAPI.getInst().getFartingPlayers().containsKey(fartPlayer.getPlayer().getUniqueId());
            if (delta - range >= 1000 * 30 && !hasTask) { // too long! delta > range + 1000*10
                Bukkit.broadcastMessage(fartPlayer.getPlayer().getDisplayName() + " 忍不住了，就地拉了出来。");
                fartPlayer.getPlayer().sendTitle(ChatColor.RED + "喷泻而出", "你喷的满地都是", 10, 20, 10); // clear title
                fartPlayer.doFartOrPoop(true);
            }
        }
    }
}
