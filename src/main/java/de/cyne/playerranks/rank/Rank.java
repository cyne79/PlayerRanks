package de.cyne.playerranks.rank;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Rank {

    private String name;
    private String prefix;
    private String suffix;
    private String chatFormat;
    private int priority;
    private boolean defaultRank;
    private ArrayList<Player> players;

    public Rank(String name, String prefix, String suffix, String chatFormat, int priority) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.chatFormat = chatFormat;
        this.priority = priority;
        this.defaultRank = false;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isDefaultRank() {
        return defaultRank;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setChatFormat(String chatFormat) {
        this.chatFormat = chatFormat;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDefaultRank(boolean defaultRank) {
        this.defaultRank = defaultRank;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
