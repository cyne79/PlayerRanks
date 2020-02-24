package de.cyne.playerranks.rank;

import de.cyne.playerranks.misc.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Inventories {

    public static void openRankInventory(Player player, int site) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, "§bRanks §8× §7Total§8: §f" + RankManager.ranks.size());

        int size = RankManager.ranks.size();
        int maxSite = (size == 0 ? 1 : (size / 27) + (size % 27 == 0 ? 0 : 1));

        if (site > maxSite && site > 1) return;
        if (!(size > (site - 1) * 8) && site > 1) return;

        for (Rank rank : RankManager.ranks) {
            String prefix = ChatColor.translateAlternateColorCodes('&', rank.getPrefix());
            String suffix = ChatColor.translateAlternateColorCodes('&', rank.getSuffix());
            String chat_format = ChatColor.translateAlternateColorCodes('&', rank.getChatFormat().replace("%player%", "Player").replace("%message%", "Message"));

            for (int i = 0; i < 27; i++) {
                int pos = (27 * (site - 1)) + i;
                int slot = i;
            }

            ItemBuilder rankItem = new ItemBuilder(Material.PAPER);
            rankItem.setDisplayName("§f" + rank.getName());
            rankItem.setLore("§8§m-------------------------", "§7Prefix §8► §r" + prefix, "§7Suffix §8► §r" + suffix, "§7Chat Format §8► §r" + chat_format, "§7Default §8► §r" + (rank.isDefaultRank() ? "§a✔" : "§c✘"));

            inventory.addItem(rankItem);
        }


        ItemBuilder placeholder;
        try {
            placeholder = new ItemBuilder(Material.valueOf("BLACK_STAINED_GLASS_PANE"));
        } catch (Exception ex) {
            placeholder = new ItemBuilder(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 15);
        }
        placeholder.setDisplayName("§0");

        for (int i = 27; i <= 35; i++) {
            inventory.setItem(i, placeholder);
        }

        Material skull;
        try{
            skull = Material.valueOf("PLAYER_HEAD");
        } catch (Exception ex) {
            skull = Material.valueOf("SKULL_ITEM");
        }

        ItemBuilder currentPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        currentPage.setDisplayName("§8► §7Current Page:");
        inventory.setItem(40, currentPage);

        ItemBuilder nextPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        nextPage.setDisplayName("§8► §7Next Page");
        inventory.setItem(42, nextPage);

        ItemBuilder previousPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        previousPage.setDisplayName("§8► §7Previous Page");
        inventory.setItem(38, previousPage);

        player.openInventory(inventory);

    }

}
