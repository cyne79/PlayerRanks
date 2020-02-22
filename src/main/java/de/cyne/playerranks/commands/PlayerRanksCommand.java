package de.cyne.playerranks.commands;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.Rank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

public class PlayerRanksCommand implements CommandExecutor, TabExecutor {


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
                    sender.sendMessage("");
                    sender.sendMessage("§8┃ §b● §8┃ §bRanks §8× §7Total§8: §f" + PlayerRanks.teams.size());
                    sender.sendMessage("§8┃ §b● §8┃ ");

                    if (PlayerRanks.teams.isEmpty()) {
                        sender.sendMessage("§8┃ §b● §8┃ §cCurrently, there are no ranks set.");
                    } else {
                        for (Entry<String, Team> team : PlayerRanks.teams.entrySet()) {
                            if (PlayerRanks.shortened.contains(team.getValue())) {
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    TextComponent message = new TextComponent("§8┃ §b● §8┃ §8- §f" + team.getKey() + " §8┃ §7Prefix §8► §8§r" + team.getValue().getPrefix() + " §8┃ ");
                                    TextComponent click = new TextComponent("§c⚠");

                                    click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §4WARNING§8: §cThis prefix was shortened because it is too long. §8┃ §7Max. chars§8: §f16").create()));
                                    message.addExtra(click);

                                    player.spigot().sendMessage(message);
                                } else {
                                    sender.sendMessage("§8┃ §b● §8┃ §8- §f" + team.getKey() + " §8┃ §7Prefix §8► §8§r" + team.getValue().getPrefix() + " §8┃ §4WARNING§8: §cThis prefix was shortened because it is too long. §8┃ §7Max. chars§8: §f16");
                                }
                            } else {
                                sender.sendMessage("§8┃ §b● §8┃ §8- §f" + team.getKey() + " §8┃ §7Prefix §8► §8§r" + team.getValue().getPrefix());
                            }

                        }
                    }
                    sender.sendMessage("");
                    return true;
                }
                if (args[0].equalsIgnoreCase("set")) {
                    sender.sendMessage(PlayerRanks.prefix + "§cUsage§8: /§cplayerranks set §8<§cplayer§8> §8<§crank§8>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage(PlayerRanks.prefix + "§cUsage§8: /§cplayerranks info §8<§cplayer§8>");
                    return true;
                }
                this.sendPluginHelp(sender);
                return true;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    sender.sendMessage(PlayerRanks.prefix + "§cUsage§8: /§cplayerranks set §8<§cplayer§8> §8<§crank§8>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("info")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(PlayerRanks.prefix + "§cThis player is not online.");
                        return true;
                    }
                    Rank rank = PlayerRanks.ranks.get(target);
                    sender.sendMessage("");
                    sender.sendMessage("§8┃ §b● §8┃ §bPlayerInfo §8× §7Player§8: §f" + target.getName());
                    sender.sendMessage("§8┃ §b● §8┃ ");
                    sender.sendMessage("§8┃ §b● §8┃ §7Rank§8: §r" + rank.getTeam().getPrefix());
                    sender.sendMessage("§8┃ §b● §8┃ §7Displayed rank§8: §r" + (rank.hasFakeTeam() ? rank.getFakeTeam().getPrefix() : rank.getTeam().getPrefix()));
                    sender.sendMessage("");
                    return true;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(PlayerRanks.prefix + "§cThis player is not online.");
                        return true;
                    }
                    if (PlayerRanks.teams.containsKey(args[2])) {
                        PlayerRanks.ranks.get(target).setFakeTeam(PlayerRanks.teams.get(args[2]));
                        sender.sendMessage(PlayerRanks.prefix + "§e" + target.getName() + " §7will now be displayed as§8: " + PlayerRanks.teams.get(args[2]).getPrefix() + target.getName());
                        PlayerRanks.chatFormat.put(target, args[2]);
                        return true;
                    }
                    sender.sendMessage(PlayerRanks.prefix + "§cThis rank does not exist. Use §8/§fpr ranks §cfor a list of all available ranks.");
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
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks ranks §8- §7List all available ranks");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks set <player> <rank> §8- §7Temporarily display a player with a different rank");
        sender.sendMessage("§8┃ §b● §8┃ §8/§fplayerranks info <player> §8- §7Information about a player's ranks");
        sender.sendMessage("");
    }

    private void reload(CommandSender sender) {
        long start = System.currentTimeMillis();
        sender.sendMessage("");
        sender.sendMessage(PlayerRanks.prefix + "§cReloading§8..");

        for (Rank rank : PlayerRanks.ranks.values()) {
            rank.remove();
        }
        for (Team team : PlayerRanks.board.getTeams()) {
            team.unregister();
        }
        PlayerRanks.teams.clear();
        PlayerRanks.priorities.clear();
        PlayerRanks.chatFormat.clear();
        PlayerRanks.defaultRank = "";

        try {
            PlayerRanks.cfg.load(PlayerRanks.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        PlayerRanks.getInstance().loadRanks();

        for (Player players : Bukkit.getOnlinePlayers()) {
            PlayerRanks.getInstance().setRank(players);
        }

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
            return getSuggestions(args[0], "reload", "ranks", "set", "info");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set") | args[0].equalsIgnoreCase("info")) {
                List<String> suggestions = new ArrayList<>();
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        suggestions.add(players.getName());
                    }
                }
                return suggestions;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> suggestions = new ArrayList<>();
                for (Entry<String, Team> team : PlayerRanks.teams.entrySet()) {
                    suggestions.add(team.getKey());
                }
                return getSuggestions(args[2], suggestions.toArray(new String[suggestions.size()]));
            }
        }
        return new ArrayList<>();
    }

}
