package com.codezhangborui.pixelRank.handler;

import com.codezhangborui.pixelRank.database.Database;

public class OnlineTimeHandler {

    public static void incrementOnlineTime() {
//        System.out.println("Before increment: " + Database.online_time_rank);
        for (String player : Database.online_time_rank.keySet()) {
            Database.online_time_rank.compute(player, (k, currentTime) -> (currentTime == null ? 0 : currentTime) + 1);
        }
//        System.out.println("After increment: " + Database.online_time_rank);
    }
}