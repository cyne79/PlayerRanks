package de.cyne.playerranks.commands;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.misc.InventoryManager;
import de.cyne.playerranks.rank.Rank;
import de.cyne.playerranks.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;

public class PlayerRanksCommand implements CommandExecutor, TabExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("playerranks.admin")) {
            if (args.length == 0) {
                this.sendPluginHelp(sender);
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload") | args[0].equalsIgnoreCase("rl")) {
                    this.reload(sender);
                    return true;
                }
                if (args[0].equalsIgnoreCase("ranks")) {
                    InventoryManager.getInventoryManager().openOverviewRanksInventory((Player) sender, 1);
                    return true;
                }
                if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(PlayerRanks.prefix + "§cUsage§8: /§cplayerranks info §8<§cplayer§8>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("cancel") && sender instanceof Player) {
                    Player p = (Player) sender;

                    if (!PlayerRanks.rankEditors.containsKey(p)) {
                        p.sendMessage(PlayerRanks.prefix + "§7You are §cnot editing §7something currently§8.");
                        return true;
                    }
                    Rank rank = PlayerRanks.rankEditors.get(p).getRank();
                    PlayerRanks.rankEditors.remove(p);
                    p.sendMessage(PlayerRanks.prefix + "§7The process has been §ccancelled§8.");

                    if (rank == null) {
                        InventoryManager.getInventoryManager().openOverviewRanksInventory(p, InventoryManager.currentPage.containsKey(p) ? InventoryManager.currentPage.get(p) : 1);
                        return true;
                    }
                    InventoryManager.getInventoryManager().openRankInventory(p, rank);
                    return true;
                }

                this.sendPluginHelp(sender);
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("info")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(PlayerRanks.prefix + "§cThis player is not online.");
                        return true;
                    }
                    Rank rank = RankManager.players.get(target);
                    sender.sendMessage("");
                    sender.sendMessage("§8┃ §b● §8┃ §bPlayerInfo §8× §7Player§8: §f" + target.getName());
                    sender.sendMessage("§8┃ §b● §8┃ ");
                    sender.sendMessage("§8┃ §b● §8┃ §7Rank§8: §f" + rank.getName());
                    sender.sendMessage("");
                    return true;
                }
            }
            this.sendPluginHelp(sender);
            return true;
        }
        sender.sendMessage(PlayerRanks.prefix + "§cYou are not permitted to use this command.");
        return true;
    }

    private void sendPluginHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§8┃ §b● §8┃ §bPlayerRanks §8× §av"
                + PlayerRanks.getInstance().getDescription().getVersion() + " §7by cyne");
        sender.sendMessage("§8┃ §b● §8┃ ");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks reload §8- §7Reload the plugin");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks ranks §8- §7Open the Rank-Inventory");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks info <player> §8- §7Information about a player's ranks");
        sender.sendMessage("");
    }

    public static void reload(CommandSender sender) {
        long start = System.currentTimeMillis();
        sender.sendMessage("");
        sender.sendMessage(PlayerRanks.prefix + "§cReloading§8..");

        RankManager.ranks.clear();
        RankManager.players.clear();

        PlayerRanks.defaultRank = null;

        try {
            PlayerRanks.cfg.load(PlayerRanks.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        RankManager.getRankManager().loadRanks();

        for (Player players : Bukkit.getOnlinePlayers())
            RankManager.getRankManager().setRank(players);

        RankManager.getRankManager().refreshAll();

        long duration = System.currentTimeMillis() - start;
        sender.sendMessage(PlayerRanks.prefix + "§aReload finished, took §e" + duration + "ms§8.");
        sender.sendMessage("");
    }

    private List<String> getSuggestions(String argument, String... array) {
        argument = argument.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        for (String suggestion : array) {
            if (suggestion.toLowerCase().startsWith(argument)) {
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return getSuggestions(args[0], "reload", "ranks", "info");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                List<String> suggestions = new ArrayList<>();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        suggestions.add(players.getName());
                    }
                }
                return suggestions;
            }
        }
        return new ArrayList<>();
    }

}
