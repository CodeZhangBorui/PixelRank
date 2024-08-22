package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import com.codezhangborui.pixelRank.scheduler.LeaderboardHandler;
import com.codezhangborui.pixelRank.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PixelRank extends JavaPlugin {
    private static Logger logger;

    private void loadConfig() {
        Configuration.init(this);
        Configuration.setDefault("ranks.mining_rank", true);
        Configuration.setDefault("ranks.placing_rank", true);
        Configuration.setDefault("ranks.online_time_rank", true);
        Configuration.setDefault("ranks.death_rank", false);
        Configuration.setDefault("ranks.switch_interval", 15);
        Configuration.setDefault("storage.database", "database.db");
        Configuration.setDefault("storage.save_interval", 60);
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("\033[96mPixelRank Version " + getDescription().getVersion() + ". Hello!\033[0m");
        logger.info("\033[32mLoading configuration...\033[0m");
        loadConfig();
        logger.info("\033[32mRecovering data from the database..\033[0m");
        Database.init(this);
        if (!Database.load()) {
            logger.severe("Failed to load data from the database. Plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LeaderboardHandler.updateScoreboard();
        logger.info("\033[32mRegistrying events and commands...\033[0m");
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("pixelrank").setExecutor(new PixelRankCommand(this));
        logger.info("\033[32mRegistrying time scheduler...\033[0m");
        Scheduler.start(this);
        logger.info("\033[92mPixelRank has been enabled!\033[0m");
    }

    @Override
    public void onDisable() {
        logger.info("\033[32mSaving all data to the database...\033[0m");
        if(!Database.save()) {
            logger.severe("Failed to save data to the database!");
        }
        logger.info("\033[91mPixelRank has been disabled! Goodbye!\033[0m");
    }
}
