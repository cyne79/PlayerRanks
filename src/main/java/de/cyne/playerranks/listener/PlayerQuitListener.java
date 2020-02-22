package de.cyne.playerranks.listener;

import de.cyne.playerranks.PlayerRanks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        PlayerRanks.chatFormat.remove(p);
        PlayerRanks.ranks.get(p).remove();
        PlayerRanks.ranks.remove(p);
    }
}
