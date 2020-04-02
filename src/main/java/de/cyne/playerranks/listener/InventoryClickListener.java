package de.cyne.playerranks.listener;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.rank.RankEditor;
import de.cyne.playerranks.misc.InventoryManager;
import de.cyne.playerranks.rank.Rank;
import de.cyne.playerranks.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class InventoryClickListener implements Listener {

    int scheduler;
    int count = 0;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) return;

        if (e.getView().getTitle().contains("§bRanks §8× §7Page §f")) {
            e.setCancelled(true);

            String[] title = e.getView().getTitle().split("/");
            String site = title[0];
            site = ChatColor.stripColor(site.substring(site.length() - 3));

            if (e.getCurrentItem().getType().equals(Material.getMaterial("SKULL_ITEM")) | e.getCurrentItem().getType().equals(Material.getMaterial("PLAYER_HEAD"))) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §7Next Page")) {
                    InventoryManager.getInventoryManager().openOverviewRanksInventory(p, Integer.valueOf(site) + 1);
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §7Previous Page")) {
                    InventoryManager.getInventoryManager().openOverviewRanksInventory(p, Integer.valueOf(site) - 1);
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §aNew Rank")) {
                    RankEditor rankEditor = new RankEditor(p, null, RankEditor.EditType.NEW_RANK);
                    PlayerRanks.rankEditors.put(p, rankEditor);
                    p.closeInventory();
                    p.sendMessage("");
                    p.sendMessage("");
                    p.sendMessage(PlayerRanks.prefix + "§7Please enter a §cname §7for the §cnew rank§8.");
                    p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                }
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §6Reload §7PlayerRanks")) {
                    long start = System.currentTimeMillis();

                    ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
                    scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(PlayerRanks.getInstance(), () -> {
                        count++;
                        if (count > 1) count = 0;
                        if (count == 0) itemMeta.setDisplayName("§8► §cReloading§8..");
                        if (count == 1) itemMeta.setDisplayName("§8► §cReloading§8.");
                        e.getCurrentItem().setItemMeta(itemMeta);
                    }, 0L, 20L);

                    RankManager.ranks.clear();
                    RankManager.players.clear();

                    PlayerRanks.defaultRank = null;

                    try {
                        PlayerRanks.cfg.load(PlayerRanks.file);
                    } catch (IOException | InvalidConfigurationException ex) {
                        ex.printStackTrace();
                    }

                    RankManager.getRankManager().loadRanks();

                    for (Player players : Bukkit.getOnlinePlayers())
                        RankManager.getRankManager().setRank(players);

                    RankManager.getRankManager().refreshAll();

                    long duration = System.currentTimeMillis() - start;

                    Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerRanks.getInstance(), () -> {
                        Bukkit.getScheduler().cancelTask(scheduler);
                        count = 0;
                        itemMeta.setDisplayName("§8► §aReload finished, took §e" + duration + "ms§8.");
                        e.getCurrentItem().setItemMeta(itemMeta);
                    }, 40L);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerRanks.getInstance(), () -> {
                        if (p.getOpenInventory().getTitle().contains("§bRanks §8× §7Page §f"))
                            InventoryManager.getInventoryManager().openOverviewRanksInventory(p, 1);
                    }, 100L);
                }

            }

            if (e.getCurrentItem().getType().equals(Material.getMaterial("BOOK"))) {
                String name = e.getCurrentItem().getItemMeta().getDisplayName();
                name = ChatColor.stripColor(name);

                for (Rank rank : RankManager.ranks) {
                    if (rank.getName().equals(name)) {
                        InventoryManager.getInventoryManager().openRankInventory(p, rank);
                    }
                }
            }

        }

        for (Rank rank : RankManager.ranks) {
            if (e.getView().getTitle().equals("§bRankEditor §8× §7" + rank.getName())) {
                e.setCancelled(true);

                if (e.getCurrentItem().getType().equals(Material.getMaterial("SKULL_ITEM")) | e.getCurrentItem().getType().equals(Material.getMaterial("PLAYER_HEAD"))) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §7Go Back")) {
                        InventoryManager.getInventoryManager().openOverviewRanksInventory(p, (InventoryManager.currentPage.containsKey(p) ? InventoryManager.currentPage.get(p) : 1));
                    }
                }

                if (e.getCurrentItem().getType().equals(Material.getMaterial("BARRIER"))) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§8► §cDelete Rank")) {
                        RankEditor rankEditor = new RankEditor(p, rank, RankEditor.EditType.DELETE_RANK);
                        PlayerRanks.rankEditors.put(p, rankEditor);
                        p.closeInventory();
                        p.sendMessage("");
                        p.sendMessage("");
                        p.sendMessage(PlayerRanks.prefix + "§cAre you sure you want to delete the rank §8\"§f" + rank.getName() + "§8\"§c?");
                        p.sendMessage(PlayerRanks.prefix + "§cConfirm §7by typing §8\"§fCONFIRM§8\"");
                        p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    }
                }

                if (e.getCurrentItem().getType().equals(Material.getMaterial("PAPER"))) {
                    RankEditor.EditType type = null;
                    String change = "";
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Prefix §8► §r")) {
                        if (e.isShiftClick()) {
                            this.copyToClipboard(rank.getPrefix());
                            p.sendMessage(PlayerRanks.prefix + "§7The prefix has been §acopied to the clipboard§8.");
                            return;
                        }
                        type = RankEditor.EditType.PREFIX;
                        change = "prefix";
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Suffix §8► §r")) {
                        if (e.isShiftClick()) {
                            this.copyToClipboard(rank.getSuffix());
                            p.sendMessage(PlayerRanks.prefix + "§7The suffix has been §acopied to the clipboard§8.");
                            return;
                        }
                        type = RankEditor.EditType.SUFFIX;
                        change = "suffix";
                    }
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Chat Format §8► §r")) {
                        if (e.isShiftClick()) {
                            this.copyToClipboard(rank.getChatFormat());
                            p.sendMessage(PlayerRanks.prefix + "§7The chat format has been §acopied to the clipboard§8.");
                            return;
                        }
                        RankEditor rankEditor = new RankEditor(p, rank, RankEditor.EditType.CHAT_FORMAT);
                        PlayerRanks.rankEditors.put(p, rankEditor);
                        p.closeInventory();
                        p.sendMessage("");
                        p.sendMessage("");
                        p.sendMessage(PlayerRanks.prefix + "§7Please enter a §cnew chat format §7(Placeholders: %player%, %message%)§8.");
                        p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    }
                    if (type != null) {
                        RankEditor rankEditor = new RankEditor(p, rank, type);
                        PlayerRanks.rankEditors.put(p, rankEditor);
                        p.closeInventory();
                        p.sendMessage("");
                        p.sendMessage("");
                        p.sendMessage(PlayerRanks.prefix + "§7Please enter a §cnew " + change + "§8.");
                        p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    }
                }

                if (e.getCurrentItem().getType().equals(Material.getMaterial("STONE_BUTTON"))) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Priority §8► §r")) {
                        RankEditor rankEditor = new RankEditor(p, rank, RankEditor.EditType.PRIORITY);
                        PlayerRanks.rankEditors.put(p, rankEditor);
                        p.closeInventory();
                        p.sendMessage("");
                        p.sendMessage("");
                        p.sendMessage(PlayerRanks.prefix + "§7Please enter a §cnew priority §7(number)§8.");
                        p.sendMessage(PlayerRanks.prefix + "§7The lower the priority, the higher the rank is displayed in the tablist.");
                        p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    }
                }

                if (e.getCurrentItem().getType().equals(Material.getMaterial("LEVER"))) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Default Rank §8► §r")) {
                        RankEditor rankEditor = new RankEditor(p, rank, RankEditor.EditType.DEFAULT_RANK);
                        PlayerRanks.rankEditors.put(p, rankEditor);
                        p.closeInventory();
                        p.sendMessage("");
                        p.sendMessage("");
                        p.sendMessage(PlayerRanks.prefix + "§7Please enter if this rank should be the §cdefault rank §7(true/false)§8.");
                        p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    }
                }

                if (e.getCurrentItem().getType().equals(Material.getMaterial("IRON_DOOR"))) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§7Permission §8► §r")) {
                        if (e.isShiftClick()) {
                            this.copyToClipboard("playerranks.rank." + rank.getName().toLowerCase());
                            p.sendMessage(PlayerRanks.prefix + "§7The permission has been §acopied to the clipboard§8.");
                            return;
                        }
                    }
                }

            }
        }

    }

    private void copyToClipboard(String str) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection strSel = new StringSelection(str);
        clipboard.setContents(strSel, null);
    }

}
