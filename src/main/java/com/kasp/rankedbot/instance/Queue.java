package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.instance.cache.QueuesCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Queue {

    private String ID;
    private int playersEachTeam;
    private PickingMode pickingMode;
    private boolean casual;
    List<Player> players;

    public Queue(String ID) {
        this.ID = ID;

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/queues/" + ID));

            this.playersEachTeam = Integer.parseInt(data.get("players-each-team").toString());
            this.pickingMode = PickingMode.valueOf(data.get("picking-mode").toString().toUpperCase());
            this.casual = Boolean.parseBoolean(data.get("casual").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        players = new ArrayList<>();

        QueuesCache.initializeQueue(ID, this);
    }

    public void addPlayer(Player player) {
        players.add(player);

        if (players.size() == playersEachTeam * 2) {
            new Game(players, this).pickTeams();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
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
    public String getID() {
        return ID;
    }
}
