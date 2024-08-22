package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EventListener implements Listener {

    private final JavaPlugin plugin;

    public EventListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        initializePlayerData(playerName);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        initializePlayerData(playerName);
        Database.mining_rank.put(playerName, Database.mining_rank.getOrDefault(playerName, 0L) + 1);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String playerName = event.getPlayer().getName();
        initializePlayerData(playerName);
        Database.placing_rank.put(playerName, Database.placing_rank.getOrDefault(playerName, 0L) + 1);
    }

    private void initializePlayerData(String playerName) {
//        plugin.getLogger().info("Initializing player data for " + playerName);
        Database.mining_rank.putIfAbsent(playerName, 0L);
        Database.placing_rank.putIfAbsent(playerName, 0L);
        Database.online_time_rank.putIfAbsent(playerName, 0L);
    }
}