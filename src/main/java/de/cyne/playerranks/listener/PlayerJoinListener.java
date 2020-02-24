package de.cyne.playerranks.listener;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.rank.RankManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        RankManager.getRankManager().setRank(p);
        RankManager.getRankManager().refreshAll();

        if (PlayerRanks.updateAvailable && PlayerRanks.cfg.getBoolean("update_notification") && p.hasPermission("playerranks.admin")) {
            TextComponent message = new TextComponent(PlayerRanks.prefix + "§7Download now §8▶ ");
            TextComponent click = new TextComponent("§8*§aclick§8*");

            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §7Redirect to §bhttps://spigotmc.org/").create()));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://spigotmc.org/resources/65769/"));

            message.addExtra(click);

            p.sendMessage("");
            p.sendMessage(PlayerRanks.prefix + "§7A §anew update §7for §bPlayerRanks §7was found§8.");
            p.spigot().sendMessage(message);
            p.sendMessage("");
        }
    }

}
