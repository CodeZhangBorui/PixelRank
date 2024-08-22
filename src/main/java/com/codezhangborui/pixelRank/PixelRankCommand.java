package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelRankCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public PixelRankCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /pixelrank reload");
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            Configuration.reload();
            if(!Database.save()) {
                plugin.getLogger().severe("Failed to save data to the database!");
            }
            sender.sendMessage("PixelRank configuration reloaded.");
            return true;
        }

        sender.sendMessage("Unknown command. Usage: /pixelrank reload");
        return false;
    }
}