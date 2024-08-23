package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class PixelRankCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;

    public PixelRankCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private void sendRank(CommandSender sender, HashMap<String, Long> rankMap) {
        AtomicInteger rankCounter = new AtomicInteger(1);
        Pattern ignorePattern = Pattern.compile(Configuration.getString("ranks.ignore_username_regex"));
        rankMap.entrySet().stream()
                .filter(entry -> !ignorePattern.matcher(entry.getKey()).matches())
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(50)
                .forEach(entry -> {
                    int i = rankCounter.getAndIncrement();
                    String player = entry.getKey();
                    String message = "";
                    if (i == 1) {
                        message += "§6";
                    } else if (i == 2) {
                        message += "§e";
                    } else if (i == 3) {
                        message += "§a";
                    } else {
                        message += "§7";
                    }
                    if (sender instanceof Player senderPlayer) {
                        if (senderPlayer.getName().equals(player)) {
                            message += ">§d";
                        } else {
                            message += " ";
                        }
                    }
                    message += i + " | " + player + " - " + entry.getValue();
                    sender.sendMessage(message);
                });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§bThe server is running PixelRank version " + plugin.getDescription().getVersion());
            sender.sendMessage("Use §b/pixelrank help§r for more information.");
            return true;
        } else if(args.length == 1 && args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§bPixelRank Help:");
            sender.sendMessage("§b/pixelrank help§r - Show this help message.");
            sender.sendMessage("§b/pixelrank rank <mine|place|time|death>§r - Show the specific rank.");
            if(sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§b/pixelrank reload§r - Reload the configuration file.");
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
        } else if(args[0].equalsIgnoreCase("rank")) {
            if(args.length == 1) {
                sender.sendMessage("Please specify the rank type:");
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("§b/pixelrank rank mine§r - Show the mining rank.");
                    sender.sendMessage("§b/pixelrank rank place§r - Show the placing rank.");
                    sender.sendMessage("§b/pixelrank rank time§r - Show the online time rank.");
                    sender.sendMessage("§b/pixelrank rank death§r - Show the death rank.");
                } else {
                    TextComponent message = new TextComponent("");
                    TextComponent button_mining = new TextComponent("[Mining] ");
                    button_mining.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pixelrank rank mine"));
                    button_mining.setColor(ChatColor.GOLD);
                    message.addExtra(button_mining);
                    TextComponent button_placing = new TextComponent("[Placing] ");
                    button_placing.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pixelrank rank place"));
                    button_placing.setColor(ChatColor.AQUA);
                    message.addExtra(button_placing);
                    TextComponent button_time = new TextComponent("[Online Time] ");
                    button_time.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pixelrank rank time"));
                    button_time.setColor(ChatColor.GREEN);
                    message.addExtra(button_time);
                    TextComponent button_death = new TextComponent("[Death] ");
                    button_death.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pixelrank rank death"));
                    button_death.setColor(ChatColor.RED);
                    message.addExtra(button_death);
                    sender.spigot().sendMessage(message);
                }
                return true;
            } else {
                if(args[1].equalsIgnoreCase("mine")) {
                    sender.sendMessage("§bMining Rank:");
                    sendRank(sender, Database.mining_rank);
                    return true;
                } else if(args[1].equalsIgnoreCase("place")) {
                    sender.sendMessage("§bPlacing Rank:");
                    sendRank(sender, Database.placing_rank);
                    return true;
                } else if(args[1].equalsIgnoreCase("time")) {
                    sender.sendMessage("§bOnline Time Rank:");
                    sendRank(sender, Database.online_time_rank);
                    return true;
                } else if(args[1].equalsIgnoreCase("death")) {
                    sender.sendMessage("§bDeath Rank:");
                    sendRank(sender, Database.death_rank);
                    return true;
                } else {
                    sender.sendMessage("§cUnknown rank type.§r Use §b/pixelrank help§r for more information.");
                    return false;
                }
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
            suggestions.add("help");
            suggestions.add("rank");
            if(sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                suggestions.add("reload");
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("rank")) {
            suggestions.add("mine");
            suggestions.add("place");
            suggestions.add("time");
            suggestions.add("death");
        }
        return suggestions;
    }
}