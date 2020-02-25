package de.cyne.playerranks.rank;

import org.bukkit.entity.Player;

public class RankEditor {

    private Player player;
    private Rank rank;
    private EditType editType;

    public RankEditor(Player player, Rank rank, EditType editType) {
        this.player = player;
        this.rank = rank;
        this.editType = editType;
    }

    public Player getPlayer() {
        return player;
    }

    public Rank getRank() {
        return rank;
    }

    public EditType getEditType() {
        return editType;
    }

    public enum EditType {
        PREFIX, SUFFIX, CHAT_FORMAT, PRIORITY, DEFAULT_RANK, NEW_RANK, DELETE_RANK
    }

}
