package com.codezhangborui.pixelRank.database;

import com.codezhangborui.pixelRank.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    public static HashMap<String, Long> mining_rank = new HashMap<>();
    public static HashMap<String, Long> placing_rank = new HashMap<>();
    public static HashMap<String, Long> online_time_rank = new HashMap<>();
    public static HashMap<String, Long> death_rank = new HashMap<>();
    private static Connection connection;
    private static String DATABASE_URL;
    private static JavaPlugin plugin;
    private static Logger logger;

    public static void init(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        logger = plugin.getLogger();
        DATABASE_URL = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + Configuration.getString("storage.database");
    }

    public static boolean load() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            if (connection != null) {
                // Create three tables if not exist
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS mining_rank (player TEXT PRIMARY KEY, value INTEGER)");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS placing_rank (player TEXT PRIMARY KEY, value INTEGER)");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS online_time_rank (player TEXT PRIMARY KEY, value INTEGER)");
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS death_rank (player TEXT PRIMARY KEY, value INTEGER)");
                // Load data from the database
                var miningRankResultSet = connection.createStatement().executeQuery("SELECT * FROM mining_rank");
                while (miningRankResultSet.next()) {
                    mining_rank.put(miningRankResultSet.getString("player"), miningRankResultSet.getLong("value"));
                }
                var placingRankResultSet = connection.createStatement().executeQuery("SELECT * FROM placing_rank");
                while (placingRankResultSet.next()) {
                    placing_rank.put(placingRankResultSet.getString("player"), placingRankResultSet.getLong("value"));
                }
                var onlineTimeRankResultSet = connection.createStatement().executeQuery("SELECT * FROM online_time_rank");
                while (onlineTimeRankResultSet.next()) {
                    online_time_rank.put(onlineTimeRankResultSet.getString("player"), onlineTimeRankResultSet.getLong("value"));
                }
                var deathRankResultSet = connection.createStatement().executeQuery("SELECT * FROM death_rank");
                while (deathRankResultSet.next()) {
                    death_rank.put(deathRankResultSet.getString("player"), deathRankResultSet.getLong("value"));
                }
                // Close the connection
                connection.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Could not connect to the SQLite database.", e);
        }
        return false;
    }

    public static boolean save() {
        new Thread(() -> {
            try {
                connection = DriverManager.getConnection(DATABASE_URL);
                if (connection != null) {
                    // Save data to the database
                    connection.createStatement().execute("DELETE FROM mining_rank");
                    for (var entry : mining_rank.entrySet()) {
                        connection.createStatement().execute("INSERT INTO mining_rank (player, value) VALUES ('" + entry.getKey() + "', " + entry.getValue() + ")");
                    }
                    connection.createStatement().execute("DELETE FROM placing_rank");
                    for (var entry : placing_rank.entrySet()) {
                        connection.createStatement().execute("INSERT INTO placing_rank (player, value) VALUES ('" + entry.getKey() + "', " + entry.getValue() + ")");
                    }
                    connection.createStatement().execute("DELETE FROM online_time_rank");
                    for (var entry : online_time_rank.entrySet()) {
                        connection.createStatement().execute("INSERT INTO online_time_rank (player, value) VALUES ('" + entry.getKey() + "', " + entry.getValue() + ")");
                    }
                    connection.createStatement().execute("DELETE FROM death_rank");
                    for (var entry : death_rank.entrySet()) {
                        connection.createStatement().execute("INSERT INTO death_rank (player, value) VALUES ('" + entry.getKey() + "', " + entry.getValue() + ")");
                    }
                    // Close the connection
                    connection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Could not connect to the SQLite database!", e);
            }
        }).start();
        return true;
    }
}