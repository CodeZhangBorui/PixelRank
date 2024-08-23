package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PixelRankCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public PixelRankCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§bThe server is running PixelRank version " + plugin.getDescription().getVersion());
            if(sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Use §b/pixelrank help§r for more information.");
            } else {
                sender.sendMessage("§cYou do not have permission to use any sub command.");
            }
            return true;
        } else if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if(sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                Configuration.reload();
                if(!Database.save()) {
                    plugin.getLogger().severe("Failed to save data to the database!");
                }
                sender.sendMessage("§aPixelRank reloaded.");
                return true;
            } else {
                sender.sendMessage("§cYou do not have permission to use this command.");
                return true;
            }
        } else {
            sender.sendMessage("§cUnknown command.§r Use §b/pixelrank help§r for more information.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if(sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                suggestions.add("reload");
            }
        }
        return suggestions;
    }
}