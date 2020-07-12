package de.cyne.playerranks.rank;

import de.cyne.playerranks.PlayerRanks;
import de.cyne.playerranks.packets.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class RankManager {

    public static HashMap<Player, Rank> players = new HashMap<>();
    public static ArrayList<Rank> ranks = new ArrayList<>();

    private static Constructor<?> chatComponentText;
    private static Class<? extends Enum> typeEnumChatFormat;

    private static RankManager rankManager;

    public RankManager() {
        rankManager = this;
    }

    public void loadRanks() {
        if (PlayerRanks.cfg.get("ranks") != null) {
            for (String rankName : PlayerRanks.cfg.getConfigurationSection("ranks").getKeys(false)) {
                Rank rank = new Rank(rankName, null, null, null, -1);

                String prefix = PlayerRanks.cfg.getString("ranks." + rankName + ".prefix");
                String suffix = PlayerRanks.cfg.getString("ranks." + rankName + ".suffix");
                String chatFormat = PlayerRanks.cfg.getString("ranks." + rankName + ".chat_format");
                int priority = PlayerRanks.cfg.getInt("ranks." + rankName + ".priority");

                if (PacketManager.getPacketManager().isLegacyVersion()) {
                    if (prefix.length() > 16) {
                        prefix = prefix.substring(0, 16);
                    }
                    if (suffix.length() > 16) {
                        suffix = suffix.substring(0, 16);
                    }
                }

                rank.setPrefix(prefix);
                rank.setSuffix(suffix);
                rank.setChatFormat(chatFormat);
                rank.setPriority(priority);

                if (PlayerRanks.cfg.getBoolean("ranks." + rankName + ".default")) {
                    rank.setDefaultRank(true);
                    PlayerRanks.defaultRank = rank;
                }

                ranks.add(rank);
            }
        }
    }

    public void setRank(Player player) {
        boolean hasRank = false;
        for (Rank rank : ranks) {
            if (player.hasPermission("playerranks.rank." + rank.getName())) {
                if (players.containsKey(player)) {
                    if (players.get(player).getPriority() > rank.getPriority()) {
                        players.get(player).removePlayer(player);
                        players.remove(player);
                        rank.addPlayer(player);
                        players.put(player, rank);
                    }
                } else {
                    rank.addPlayer(player);
                    players.put(player, rank);
                }
                hasRank = true;
            }
        }
        if (!hasRank) {
            if (PlayerRanks.defaultRank != null) {
                PlayerRanks.defaultRank.addPlayer(player);
                players.put(player, PlayerRanks.defaultRank);
            }
        }
    }

    public void sendScoreboardPackets(int state) {
        for (Rank rank : ranks) {
            String name = rank.getName();
            String prefix = "";
            String suffix = "";
            int priority = 1000 + rank.getPriority();
            String teamName = priority + name;

            Collection<String> entries = new ArrayList<>();
            for (Player players : rank.getPlayers()) {

                prefix = PlayerRanks.getFormattedMessage(players, rank.getPrefix());
                suffix = PlayerRanks.getFormattedMessage(players, rank.getSuffix());

                entries.add(players.getName());
            }

            try {
                if (!PacketManager.getPacketManager().isLegacyVersion()) {
                    Class<?> typeChatComponentText = Class.forName("net.minecraft.server." + this.getVersion() + ".ChatComponentText");
                    chatComponentText = typeChatComponentText.getConstructor(String.class);
                    typeEnumChatFormat = (Class<? extends Enum>) Class.forName("net.minecraft.server." + this.getVersion() + ".EnumChatFormat");
                }

                Constructor<?> constructor = getNMSClass("PacketPlayOutScoreboardTeam").getConstructor((Class<?>[]) new Class[0]);
                Object packet = constructor.newInstance();

                if (PacketManager.getPacketManager().isLegacyVersion()) {
                    PacketManager.DISPLAY_NAME.set(packet, name);
                    PacketManager.PREFIX.set(packet, prefix);
                    PacketManager.SUFFIX.set(packet, suffix);
                } else {
                    PacketManager.DISPLAY_NAME.set(packet, chatComponentText.newInstance(name));
                    PacketManager.PREFIX.set(packet, chatComponentText.newInstance(prefix));
                    PacketManager.SUFFIX.set(packet, chatComponentText.newInstance(suffix));

                    String s = PlayerRanks.cfg.getString("ranks." + rank.getName() + ".prefix");
                    if (s.contains("&")) {
                        String[] colors = s.split("&");
                        String lastColor = colors[colors.length - 1].replaceAll(" ", "");
                        String chatColor = ChatColor.getByChar(lastColor).name();

                        Enum<?> colorEnum = Enum.valueOf(typeEnumChatFormat, chatColor);
                        PacketManager.TEAM_COLOR.set(packet, colorEnum);
                    }
                }
                PacketManager.ENTRIES.set(packet, entries);
                PacketManager.TEAM_NAME.set(packet, teamName);
                PacketManager.PACK_OPTION.set(packet, 1);

                if (PacketManager.VISIBILITY != null) {
                    PacketManager.VISIBILITY.set(packet, "always");
                }

                PacketManager.PARAM_INT.set(packet, state);

                for (Player players : Bukkit.getOnlinePlayers())
                    sendPacket(players, packet);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void refreshAll() {
        this.sendScoreboardPackets(1);
        this.sendScoreboardPackets(0);
    }

    private Class<?> getNMSClass(String name) {
        String version = this.getVersion();
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object playerHandle = player.getClass().getMethod("getHandle", (Class<?>[]) new Class[0]).invoke(player);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RankManager getRankManager() {
        return rankManager;
    }
}

