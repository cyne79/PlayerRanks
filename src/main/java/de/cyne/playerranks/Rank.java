package de.cyne.playerranks;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class Rank {

    private Player player;
    private Team team;
    private Team fakeTeam;
    private int priority;

    public Rank(Player player) {
        this.player = player;
        this.team = null;
        this.fakeTeam = null;
        this.priority = -1;
    }

    public void remove() {
        if (this.team != null)
            this.team.removeEntry(this.player.getName());

        if (this.fakeTeam != null)
            this.fakeTeam.removeEntry(this.player.getName());

        this.team = null;
        this.fakeTeam = null;

        this.priority = -1;
    }

    public void setTeam(Team team, int priority) {
        if (this.team != null)
            this.team.removeEntry(this.player.getName());

        this.priority = priority;
        this.team = team;
        this.team.addEntry(this.player.getName());
    }

    public void setFakeTeam(Team team) {
        if (this.team != null)
            this.team.removeEntry(this.player.getName());
        if (team == null) {
            if (this.fakeTeam == null) return;
            this.fakeTeam.removeEntry(this.player.getName());
            if (this.team != null)
                this.team.removeEntry(this.player.getName());
            this.fakeTeam = null;
        } else {
            this.fakeTeam = team;
            this.fakeTeam.addEntry(this.player.getName());
        }
    }

    public Team getTeam() {
        return this.team;
    }

    public Team getFakeTeam() {
        return fakeTeam;
    }

    public boolean hasFakeTeam() {
        return this.fakeTeam != null;
    }

    public int getPriority() {
        return this.priority;
    }
}