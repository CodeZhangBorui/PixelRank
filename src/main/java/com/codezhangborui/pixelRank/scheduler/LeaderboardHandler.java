package com.codezhangborui.pixelRank.scheduler;

import com.codezhangborui.pixelRank.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public class LeaderboardHandler {
    public static int currentLeaderboard = 0;

    public static void switchLeaderboard() {
        currentLeaderboard = (currentLeaderboard + 1) % 3;
        updateScoreboard();
    }

    public static void updateScoreboard() {
//        System.out.println("Updating scoreboard...");
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            return;
        }
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("leaderboard", "dummy", "Leaderboard");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        switch (currentLeaderboard) {
            case 0:
                objective.setDisplayName("§f§m-§7§m-§8§m[-§r Mining Rank §8§m-]§7§m-§f§m-");
                updateScores(objective, Database.mining_rank);
                break;
            case 1:
                objective.setDisplayName("§f§m-§7§m-§8§m[-§r Placing Rank §8§m-]§7§m-§f§m-");
                updateScores(objective, Database.placing_rank);
                break;
            case 2:
                objective.setDisplayName("§f§m-§7§m-§8§m[-§r Online Time Rank §8§m-]§7§m-§f§m-");
                updateScores(objective, Database.online_time_rank);
                break;
        }

        Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(scoreboard));
    }

    private static void updateScores(Objective objective, HashMap<String, Long> rankData) {
        int currentSize = 0;
        for (String player : rankData.keySet()) {
            Score score = objective.getScore(player);
            score.setScore(rankData.get(player).intValue());
            currentSize++;
            if (currentSize >= 15) {
                break;
            }
        }
    }
}