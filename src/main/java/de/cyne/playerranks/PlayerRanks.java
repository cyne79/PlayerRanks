package de.cyne.playerranks;

import de.cyne.playerranks.commands.PlayerRanksCommand;
import de.cyne.playerranks.listener.AsyncPlayerChatListener;
import de.cyne.playerranks.listener.PlayerJoinListener;
import de.cyne.playerranks.listener.PlayerQuitListener;
import de.cyne.playerranks.misc.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class PlayerRanks extends JavaPlugin implements Listener {

    private static PlayerRanks instance;

    public static File file = new File("plugins/PlayerRanks", "config.yml");
    public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static HashMap<String, Team> teams = new HashMap<>();
    public static HashMap<String, Integer> priorities = new HashMap<>();
    public static HashMap<Player, Rank> ranks = new HashMap<>();
    public static HashMap<Player, String> chatFormat = new HashMap<>();

    public static ArrayList<Team> shortened = new ArrayList<>();

    public static String defaultRank = "";

    public static Updater updater;
    public static boolean updateAvailable = false;

    public static Scoreboard board;

    public static String version;
    public static boolean post1_13;

    public static String prefix = "§8┃ §bPlayerRanks §8┃ §r";

    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        try {
            PlayerRanks.cfg.load(PlayerRanks.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        this.registerCommands();
        this.registerListener();

        updater = new Updater(66559);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PlayerRanks.getInstance(), () -> updater.run(), 0L, 20 * 60 * 60 * 24);

        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        post1_13 = (!version.contains("v1_8") && !version.contains("v1_9") && !version.contains("v1_10") && !version.contains("v1_11") && !version.contains("v1_12"));

        board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.loadRanks();

        for (Player players : Bukkit.getOnlinePlayers()) {
            ranks.put(players, new Rank(players));
            players.setScoreboard(board);
            PlayerRanks.getInstance().setRank(players);
        }
    }

    private void registerCommands() {
        PlayerRanks.getInstance().getCommand("playerranks").setExecutor(new PlayerRanksCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), PlayerRanks.getInstance());
    }

    public void loadRanks() {
        if (PlayerRanks.cfg.get("ranks") != null) {
            for (String rank : PlayerRanks.cfg.getConfigurationSection("ranks").getKeys(false)) {
                int priority = 1000 - PlayerRanks.cfg.getInt("ranks." + rank + ".priority");

                if (!teams.containsKey(rank)) {
                    teams.put(rank, board.registerNewTeam(priority + rank));
                }
                if (PlayerRanks.cfg.getBoolean("ranks." + rank + ".default")) {
                    defaultRank = rank;
                }

                String prefix = ChatColor.translateAlternateColorCodes('&', PlayerRanks.cfg.getString("ranks." + rank + ".prefix"));


                if (!post1_13) {
                    if (prefix.length() > 16) {
                        prefix = prefix.substring(0, 16);
                        shortened.add(teams.get(rank));
                    }
                }
                teams.get(rank).setPrefix(prefix);
                priorities.put(rank, PlayerRanks.cfg.getInt("ranks." + rank + ".priority"));

                if (post1_13) {
                    String s = PlayerRanks.cfg.getString("ranks." + rank + ".prefix");
                    if (s.contains("&")) {
                        String[] colors = s.split("&");

                        String lastColor = colors[colors.length - 1].replaceAll(" ", "");

                        ChatColor color = ChatColor.getByChar(lastColor);
                        teams.get(rank).setColor(color);
                    }
                }

            }
        }
    }

    public void setRank(Player player) {
        boolean hasRank = false;
        for (Entry<String, Team> team : PlayerRanks.teams.entrySet()) {
            if (player.hasPermission("playerranks.rank." + team.getKey())) {
                if (PlayerRanks.ranks.get(player).getTeam() != null) {
                    if (PlayerRanks.ranks.get(player).getPriority() < PlayerRanks.priorities.get(team.getKey())) {
                        PlayerRanks.ranks.get(player).setTeam(team.getValue(), PlayerRanks.priorities.get(team.getKey()));
                        PlayerRanks.chatFormat.put(player, team.getKey());
                    }
                } else {
                    PlayerRanks.ranks.get(player).setTeam(team.getValue(), PlayerRanks.priorities.get(team.getKey()));
                    PlayerRanks.chatFormat.put(player, team.getKey());
                }
                hasRank = true;
            }
        }
        if (!hasRank) {
            if (!PlayerRanks.defaultRank.equals("")) {
                PlayerRanks.ranks.get(player).setTeam(PlayerRanks.teams.get(PlayerRanks.defaultRank), PlayerRanks.priorities.get(PlayerRanks.defaultRank));
                PlayerRanks.chatFormat.put(player, PlayerRanks.defaultRank);
            }
        }
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static PlayerRanks getInstance() {
        return instance;
    }

}

