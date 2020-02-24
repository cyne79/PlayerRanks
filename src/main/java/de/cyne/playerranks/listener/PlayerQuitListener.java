package de.cyne.playerranks.listener;

import de.cyne.playerranks.rank.RankManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (RankManager.players.containsKey(p)) {
            RankManager.players.get(p).removePlayer(p);
            RankManager.players.remove(p);
        }

    }
}
