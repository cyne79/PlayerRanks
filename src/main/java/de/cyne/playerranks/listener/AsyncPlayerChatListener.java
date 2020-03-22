package de.cyne.playerranks.listener;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.rank.RankEditor;
import de.cyne.playerranks.misc.InventoryManager;
import de.cyne.playerranks.rank.Rank;
import de.cyne.playerranks.rank.RankManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");

        if (PlayerRanks.cfg.getBoolean("use_chat_format")) {
            if (p.hasPermission("playerranks.chatcolor")) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            Rank rank = RankManager.players.get(p);

            if (rank != null && PlayerRanks.cfg.get("ranks." + rank.getName() + ".chat_format") != null) {
                e.setFormat(ChatColor.translateAlternateColorCodes('&', rank.getChatFormat()).replace("%player%", p.getName()).replace("%message%", message));
            }
        }

        // > RankEditor
        if (PlayerRanks.rankEditors.containsKey(p)) {
            e.setCancelled(true);
            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.NEW_RANK) {
                int highestPriority = 0;
                for (Rank existingRank : RankManager.ranks) {
                    if (e.getMessage().equalsIgnoreCase(existingRank.getName())) {
                        p.sendMessage(PlayerRanks.prefix + "§cA rank with this name already exists.");
                        return;
                    }
                    if(existingRank.getPriority() > highestPriority) highestPriority = existingRank.getPriority();
                }

                Rank rank = new Rank(e.getMessage(), "", "", "&7%player% &8» §r%message%", highestPriority + 1);
                RankManager.ranks.add(rank);
                rank.saveToConfiguration();

                this.saveConfig();
                PlayerRanks.rankEditors.remove(p);
                p.sendMessage(PlayerRanks.prefix + "§7The rank §8\"§f" + rank.getName() + "§8\" §7has been §aadded§8.");
                InventoryManager.getInventoryManager().openRankInventory(p, rank);
                return;
            }

            String rankName = PlayerRanks.rankEditors.get(p).getRank().getName();

            String path = null;
            String change = null;

            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.PREFIX) {
                path = ".prefix";
                change = "prefix";
                PlayerRanks.rankEditors.get(p).getRank().setPrefix(e.getMessage());
            }
            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.SUFFIX) {
                path = ".suffix";
                change = "suffix";
                PlayerRanks.rankEditors.get(p).getRank().setSuffix(e.getMessage());
            }
            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.CHAT_FORMAT) {
                path = ".chat_format";
                change = "chat format";
                PlayerRanks.rankEditors.get(p).getRank().setChatFormat(e.getMessage());
            }
            if (path != null) {
                PlayerRanks.cfg.set("ranks." + rankName + path, e.getMessage());
                this.saveConfig();
                p.sendMessage(PlayerRanks.prefix + "§7The §a" + change + " §7has been changed §asuccessfully§8.");
                InventoryManager.getInventoryManager().openRankInventory(p, PlayerRanks.rankEditors.get(p).getRank());
                PlayerRanks.rankEditors.remove(p);
                return;
            }

            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.PRIORITY) {
                if (!this.isInteger(e.getMessage())) {
                    p.sendMessage(PlayerRanks.prefix + "§7You have to enter a §cnumber§8.");
                    return;
                }
                int priority = Integer.valueOf(e.getMessage());
                PlayerRanks.cfg.set("ranks." + rankName + ".priority", priority);
                this.saveConfig();
                PlayerRanks.rankEditors.get(p).getRank().setPriority(priority);
                p.sendMessage(PlayerRanks.prefix + "§7The §apriority §7has been changed §asuccessfully§8.");
                InventoryManager.getInventoryManager().openRankInventory(p, PlayerRanks.rankEditors.get(p).getRank());
                PlayerRanks.rankEditors.remove(p);
                return;
            }

            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.DEFAULT_RANK) {
                if (e.getMessage().equalsIgnoreCase("true")) {
                    PlayerRanks.cfg.set("ranks." + rankName + ".default", true);
                    this.saveConfig();
                    PlayerRanks.rankEditors.get(p).getRank().setDefaultRank(true);
                    p.sendMessage(PlayerRanks.prefix + "§8\"§aDefault rank§8\" §7has been changed §asuccessfully§8.");
                    InventoryManager.getInventoryManager().openRankInventory(p, PlayerRanks.rankEditors.get(p).getRank());
                    PlayerRanks.rankEditors.remove(p);
                } else if (e.getMessage().equalsIgnoreCase("false")) {
                    PlayerRanks.cfg.set("ranks." + rankName + ".default", false);
                    this.saveConfig();
                    PlayerRanks.rankEditors.get(p).getRank().setDefaultRank(false);
                    p.sendMessage(PlayerRanks.prefix + "§8\"§aDefault rank§8\" §7has been changed §asuccessfully§8.");
                    InventoryManager.getInventoryManager().openRankInventory(p, PlayerRanks.rankEditors.get(p).getRank());
                    PlayerRanks.rankEditors.remove(p);
                } else {
                    p.sendMessage(PlayerRanks.prefix + "§7You have to enter §ctrue §7or §cfalse§8.");
                }
                return;
            }

            if (PlayerRanks.rankEditors.get(p).getEditType() == RankEditor.EditType.DELETE_RANK) {
                if (!e.getMessage().equalsIgnoreCase("CONFIRM")) {
                    p.sendMessage(PlayerRanks.prefix + "§7Please type §8\"§fCONFIRM§8\" §7to §cdelete the rank§8.");
                    p.sendMessage(PlayerRanks.prefix + "§7You can §ccancel §7with §8\"§f/pr cancel§8\".");
                    return;
                }
                Rank rank = PlayerRanks.rankEditors.get(p).getRank();

                rank.setPlayers(null);

                RankManager.ranks.remove(rank);
                RankManager.players.remove(rank);

                PlayerRanks.cfg.set("ranks." + rankName, null);
                this.saveConfig();

                p.sendMessage(PlayerRanks.prefix + "§7The rank has been §cdeleted §asuccessfully§8.");
                InventoryManager.getInventoryManager().openOverviewRanksInventory(p, (InventoryManager.currentPage.containsKey(p) ? InventoryManager.currentPage.get(p) : 1));
                PlayerRanks.rankEditors.remove(p);
                return;
            }

        }

    }

    private void saveConfig() {
        try {
            PlayerRanks.cfg.save(PlayerRanks.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

}
