package de.cyne.playerranks;

import de.cyne.playerranks.commands.PlayerRanksCommand;
import de.cyne.playerranks.expansions.PlayerRanksExpansions;
import de.cyne.playerranks.listener.*;
import de.cyne.playerranks.metrics.Metrics;
import de.cyne.playerranks.misc.InventoryManager;
import de.cyne.playerranks.rank.RankEditor;
import de.cyne.playerranks.misc.Updater;
import de.cyne.playerranks.packets.PacketManager;
import de.cyne.playerranks.rank.Rank;
import de.cyne.playerranks.rank.RankManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PlayerRanks extends JavaPlugin {

    private static PlayerRanks instance;

    public static File file = new File("plugins/PlayerRanks", "config.yml");
    public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static Rank defaultRank = null;

    public static PacketManager packetManager;
    public static RankManager rankManager;
    public static InventoryManager inventoryManager;

    public static Updater updater;
    public static boolean updateAvailable = false;

    public static HashMap<Player, RankEditor> rankEditors = new HashMap<>();

    public static boolean placeholderApi = false;

    public static String prefix = "§8┃ §bPlayerRanks §8┃ §f";

    public Metrics metrics;

    public void onEnable() {
        instance = this;

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderApi = true;
            new PlayerRanksExpansions(getInstance()).register();
        }

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

        packetManager = new PacketManager();
        rankManager = new RankManager();
        inventoryManager = new InventoryManager();

        RankManager.getRankManager().loadRanks();

        for (Player players : Bukkit.getOnlinePlayers())
            RankManager.getRankManager().setRank(players);

        RankManager.getRankManager().refreshAll();

        metrics = new Metrics(PlayerRanks.getInstance(), 7113);
        //metrics.addCustomChart(new Metrics.SimplePie("singleworld_mode", () -> singleWorld_mode ? "enabled" : "disabled"));
    }

    private void registerCommands() {
        PlayerRanks.getInstance().getCommand("playerranks").setExecutor(new PlayerRanksCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerRanksCommand(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), PlayerRanks.getInstance());
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), PlayerRanks.getInstance());
    }

    public static PlayerRanks getInstance() {
        return instance;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static String getFormattedMessage(Player player, String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        if(PlayerRanks.placeholderApi) {
            return PlaceholderAPI.setPlaceholders(player, message);
        } else {
            return message;
        }
    }

}