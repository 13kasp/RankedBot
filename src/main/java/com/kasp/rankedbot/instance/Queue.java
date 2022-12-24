package com.kasp.rankedbot.classes.games;

import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.classes.player.Player;

import java.util.ArrayList;

public class Queue {

    private String ID;
    private int teams;
    private int playersEachTeam;
    private PickingMode pickingMode;
    private boolean casual;
    ArrayList<Player> players = new ArrayList<>();

    public Queue(String ID) {
        this.ID = ID;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public int getTeams() {
        return teams;
    }

    public int getPlayersEachTeam() {
        return playersEachTeam;
    }

    public PickingMode getPickingMode() {
        return pickingMode;
    }

    public boolean isCasual() {
        return casual;
    }
}
