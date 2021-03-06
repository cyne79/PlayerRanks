package de.cyne.playerranks.misc;

import de.cyne.playerranks.rank.Rank;
import de.cyne.playerranks.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryManager {

    public static HashMap<Player, Integer> currentPage = new HashMap<>();

    private static InventoryManager inventoryManager;

    public InventoryManager() {
        inventoryManager = this;
    }

    public void openOverviewRanksInventory(Player player, int site) {

        List<Rank> sortedRanks = RankManager.ranks.stream().sorted(Comparator.comparing(Rank::getPriority)).collect(Collectors.toList());

        int size = sortedRanks.size();
        int maxSite = (size == 0 ? 1 : (size / 18) + (size % 18 == 0 ? 0 : 1));

        if (site > maxSite && site > 1) return;
        if (site < 1) return;
        currentPage.put(player, site);

        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "§bRanks §8× §7Page §f" + site + "§8/§f" + maxSite);

        for (int i = 0; i < 18; i++) {
            int pos = (18 * (site - 1)) + i;

            if (sortedRanks.size() - 1 >= pos) {
                Rank rank = sortedRanks.get(pos);

                String prefix = ChatColor.translateAlternateColorCodes('&', rank.getPrefix());
                String suffix = ChatColor.translateAlternateColorCodes('&', rank.getSuffix());
                String chat_format = ChatColor.translateAlternateColorCodes('&', rank.getChatFormat());
                int priority = rank.getPriority();

                ItemBuilder rankItem = new ItemBuilder(Material.getMaterial("BOOK"));
                rankItem.setDisplayName("§f" + rank.getName());
                rankItem.setLore("§8§m--------------------", "§7Prefix §8► §f" + prefix, "§7Suffix §8► §f" + suffix, "§7Chat Format §8► §f" + chat_format, "§7Priority §8► §f" + priority, "§7Default Rank §8► §f" + (rank.isDefaultRank() ? "§a✔" : "§c✘"));
                inventory.setItem(i, rankItem);
            }
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
        try {
            skull = Material.valueOf("PLAYER_HEAD");
        } catch (Exception ex) {
            skull = Material.valueOf("SKULL_ITEM");
        }

        ItemBuilder currentPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        currentPage.setDisplayName("§8► §7Current Page: §f" + site + "§8/§f" + maxSite);
        currentPage.setLore("§8§m--------------------", " §8● §7Total Ranks§8: §f" + RankManager.ranks.size());
        currentPage.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5NWQ3YzFiYmYzYWZhMjg1ZDhkOTY3NTdiYjU1NzIyNTlhM2FlODU0ZjUzODlkYzUzMjA3Njk5ZDk0ZmQ4In19fQ==");
        inventory.setItem(31, currentPage);

        ItemBuilder nextPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        nextPage.setDisplayName("§8► §7Next Page");
        nextPage.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19");
        inventory.setItem(35, nextPage);

        ItemBuilder previousPage = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        previousPage.setDisplayName("§8► §7Previous Page");
        previousPage.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
        inventory.setItem(34, previousPage);

        ItemBuilder addRank = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        addRank.setDisplayName("§8► §aNew Rank");
        addRank.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkZDIwYmU5MzUyMDk0OWU2Y2U3ODlkYzRmNDNlZmFlYjI4YzcxN2VlNmJmY2JiZTAyNzgwMTQyZjcxNiJ9fX0=");
        inventory.setItem(27, addRank);

        ItemBuilder reload = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        reload.setDisplayName("§8► §6Reload §7PlayerRanks");
        reload.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg4N2NjMzg4YzhkY2ZjZjFiYThhYTVjM2MxMDJkY2U5Y2Y3YjFiNjNlNzg2YjM0ZDRmMWMzNzk2ZDNlOWQ2MSJ9fX0=");
        inventory.setItem(29, reload);

        player.openInventory(inventory);
    }


    public void openRankInventory(Player player, Rank rank) {
        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "§bRankEditor §8× §7" + rank.getName());

        ItemBuilder placeholder;
        try {
            placeholder = new ItemBuilder(Material.valueOf("BLACK_STAINED_GLASS_PANE"));
        } catch (Exception ex) {
            placeholder = new ItemBuilder(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 15);
        }
        placeholder.setDisplayName("§0");

        inventory.setItem(11, placeholder);
        for (int i = 27; i <= 35; i++) {
            inventory.setItem(i, placeholder);
        }

        Material skull;
        try {
            skull = Material.valueOf("PLAYER_HEAD");
        } catch (Exception ex) {
            skull = Material.valueOf("SKULL_ITEM");
        }

        ItemBuilder rankItem = new ItemBuilder(Material.getMaterial("BOOK"));
        rankItem.setDisplayName("§f" + rank.getName());
        inventory.setItem(10, rankItem);

        ItemBuilder goBack = new ItemBuilder(skull, 1, (short) SkullType.PLAYER.ordinal());
        goBack.setDisplayName("§8► §7Go Back");
        goBack.setSkullOwner("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");
        inventory.setItem(27, goBack);

        ItemBuilder prefix = new ItemBuilder(Material.getMaterial("PAPER"));
        prefix.setDisplayName("§7Prefix §8► §f" + rank.getPrefix());
        prefix.setLore("§8§m--------------------", " §8➟ §cClick to change the §fprefix", " §8➟ §cShift-Click to copy the §fprefix");
        inventory.setItem(12, prefix);

        ItemBuilder suffix = new ItemBuilder(Material.getMaterial("PAPER"));
        suffix.setDisplayName("§7Suffix §8► §f" + rank.getSuffix());
        suffix.setLore("§8§m--------------------", " §8➟ §cClick to change the §fsuffix", " §8➟ §cShift-Click to copy the §fsuffix");
        inventory.setItem(13, suffix);

        ItemBuilder chat_format = new ItemBuilder(Material.getMaterial("PAPER"));
        chat_format.setDisplayName("§7Chat Format §8► §f" + rank.getChatFormat());
        chat_format.setLore("§8§m--------------------", " §8➟ §cClick to change the §fchat format", " §8➟ §cShift-Click to copy the §fchat format");
        inventory.setItem(14, chat_format);

        ItemBuilder priority = new ItemBuilder(Material.getMaterial("STONE_BUTTON"));
        priority.setDisplayName("§7Priority §8► §f" + rank.getPriority());
        priority.setLore("§8§m--------------------", " §8➟ §cClick to change the §fpriority");
        inventory.setItem(15, priority);

        ItemBuilder defaultRank = new ItemBuilder(Material.getMaterial("LEVER"));
        defaultRank.setDisplayName("§7Default Rank §8► §f" + (rank.isDefaultRank() ? "true" : "false"));
        defaultRank.setLore("§8§m--------------------", " §8➟ §cClick to change §f\"default rank\"");
        inventory.setItem(16, defaultRank);

        ItemBuilder deleteRank = new ItemBuilder(Material.getMaterial("BARRIER"));
        deleteRank.setDisplayName("§8► §cDelete Rank");
        inventory.setItem(31, deleteRank);

        ItemBuilder permission = new ItemBuilder(Material.getMaterial("IRON_DOOR"));
        permission.setDisplayName("§7Permission §8► §fplayerranks.rank." + rank.getName().toLowerCase());
        permission.setLore("§8§m--------------------", " §8➟ §cShift-Click to copy the §fpermission");
        inventory.setItem(34, permission);

        player.openInventory(inventory);
    }

    public static InventoryManager getInventoryManager() {
        return inventoryManager;
    }

}
