package de.cyne.playerranks.listener;

import de.cyne.playerranks.PlayerRanks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage().replace("%", "%%");

        if (PlayerRanks.cfg.getBoolean("use_chat_format")) {
            if (p.hasPermission("playerranks.chatcolor")) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            String rank = PlayerRanks.chatFormat.get(p);
            if (PlayerRanks.cfg.get("ranks." + rank + ".chat_format") != null) {
                e.setFormat(ChatColor.translateAlternateColorCodes('&', PlayerRanks.cfg.getString("ranks." + rank + ".chat_format")).replace("%player%", p.getName()).replace("%message%", message));
            }
        }

    }
}
