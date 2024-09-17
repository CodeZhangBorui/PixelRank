package com.codezhangborui.pixelRank;

import com.codezhangborui.pixelRank.database.Database;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
        rankMap.entrySet().stream().filter(entry -> !ignorePattern.matcher(entry.getKey()).matches()).sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())).limit(50).forEach(entry -> {
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
            sender.sendMessage(Component.text("The server is running PixelRank version " + plugin.getDescription().getVersion(), NamedTextColor.AQUA));
            sender.sendMessage(Component.text("Use ", NamedTextColor.WHITE)
                    .append(Component.text("/pixelrank help", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/pixelrank help")))
                    .append(Component.text(" for more information.", NamedTextColor.WHITE)));
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text();
            message.append(Component.text("PixelRank Help:", NamedTextColor.AQUA)).append(Component.newline());
            message.append(Component.text("/pixelrank help", NamedTextColor.WHITE).clickEvent(ClickEvent.runCommand("/pixelrank help"))
                    .append(Component.text(" - Show this help message.", NamedTextColor.GRAY))).append(Component.newline());
            message.append(Component.text("/pixelrank rank <mine|place|time|death>", NamedTextColor.WHITE).clickEvent(ClickEvent.runCommand("/pixelrank rank"))
                    .append(Component.text(" - Show the specific rank.", NamedTextColor.GRAY))).append(Component.newline());
            if (sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                message.append(Component.text("/pixelrank reload", NamedTextColor.WHITE).clickEvent(ClickEvent.runCommand("/pixelrank reload"))
                        .append(Component.text(" - Reload the configuration file.", NamedTextColor.GRAY)).append(Component.newline()));
            }
            sender.sendMessage(message.build());
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                Configuration.reload();
                if (!Database.save()) {
                    plugin.getLogger().severe("Failed to save data to the database!");
                }
                sender.sendMessage(Component.text("Configuration has been successfully reloaded.", NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text("You do not have permission to execute this command.", NamedTextColor.RED));
            }
            return true;
        } else if (args[0].equalsIgnoreCase("rank")) {
            if (args.length == 1) {
                sender.sendMessage(Component.text("Please specify the rank type:", NamedTextColor.AQUA));
                if (sender instanceof ConsoleCommandSender) {
                    if(Configuration.getBoolean("ranks.mining_rank")) {
                        sender.sendMessage("\033[96m/pixelrank rank mine\033[0m - Show the mining rank.");
                    }
                    if(Configuration.getBoolean("ranks.placing_rank")) {
                        sender.sendMessage("\033[96m/pixelrank rank place\033[0m - Show the placing rank.");
                    }
                    if(Configuration.getBoolean("ranks.online_time_rank")) {
                        sender.sendMessage("\033[96m/pixelrank rank time\033[0m - Show the online time rank.");
                    }
                    if(Configuration.getBoolean("ranks.death_rank")) {
                        sender.sendMessage("\033[96m/pixelrank rank death\033[0m - Show the death rank.");
                    }
                } else {
                    ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text();
                    if(Configuration.getBoolean("ranks.mining_rank")) {
                        message.append(Component.text("[Mining] ", NamedTextColor.GOLD).clickEvent(ClickEvent.runCommand("/pixelrank rank mine")));
                    }
                    if(Configuration.getBoolean("ranks.placing_rank")) {
                        message.append(Component.text("[Placing] ", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/pixelrank rank place")));
                    }
                    if(Configuration.getBoolean("ranks.online_time_rank")) {
                        message.append(Component.text("[Online Time] ", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/pixelrank rank time")));
                    }
                    if(Configuration.getBoolean("ranks.death_rank")) {
                        message.append(Component.text("[Death] ", NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/pixelrank rank death")));
                    }
                    sender.sendMessage(message.build());
                }
                return true;
            } else {
                if (args[1].equalsIgnoreCase("mine") && Configuration.getBoolean("ranks.mining_rank")) {
                    sender.sendMessage(Component.text("Mining Rank:").color(NamedTextColor.AQUA));
                    sendRank(sender, Database.mining_rank);
                    return true;
                } else if (args[1].equalsIgnoreCase("place") && Configuration.getBoolean("ranks.placing_rank")) {
                    sender.sendMessage(Component.text("Placing Rank:").color(NamedTextColor.AQUA));
                    sendRank(sender, Database.placing_rank);
                    return true;
                } else if (args[1].equalsIgnoreCase("time") && Configuration.getBoolean("ranks.online_time_rank")) {
                    sender.sendMessage(Component.text("Online Time Rank:").color(NamedTextColor.AQUA));
                    sendRank(sender, Database.online_time_rank);
                    return true;
                } else if (args[1].equalsIgnoreCase("death") && Configuration.getBoolean("ranks.death_rank")) {
                    sender.sendMessage(Component.text("Death Rank:").color(NamedTextColor.AQUA));
                    sendRank(sender, Database.death_rank);
                    return true;
                } else {
                    TextComponent message = Component.text("Unknown or disabled rank type.", NamedTextColor.RED)
                            .append(Component.text(" Use ", NamedTextColor.WHITE))
                            .append(Component.text("/pixelrank help", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/pixelrank help")))
                            .append(Component.text(" for more information.", NamedTextColor.WHITE));
                    sender.sendMessage(message);
                    return false;
                }
            }
        } else {
            TextComponent message = Component.text("Unknown command.", NamedTextColor.RED)
                            .append(Component.text(" Use ", NamedTextColor.WHITE))
                            .append(Component.text("/pixelrank help", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/pixelrank help")))
                            .append(Component.text(" for more information.", NamedTextColor.WHITE));
            sender.sendMessage(message);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            suggestions.add("help");
            suggestions.add("rank");
            if (sender.isOp() || sender.hasPermission("pixelrank.admin") || sender instanceof ConsoleCommandSender) {
                suggestions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("rank")) {
            if(Configuration.getBoolean("ranks.mining_rank")) {
                suggestions.add("mine");
            }
            if(Configuration.getBoolean("ranks.placing_rank")) {
                suggestions.add("place");
            }
            if(Configuration.getBoolean("ranks.online_time_rank")) {
                suggestions.add("time");
            }
            if(Configuration.getBoolean("ranks.death_rank")) {
                suggestions.add("death");
            }
        }
        return suggestions;
    }
}