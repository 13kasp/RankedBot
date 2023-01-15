package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.instance.cache.PartyCache;
import com.kasp.rankedbot.instance.cache.QueueCache;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Queue {

    private String ID;
    private int playersEachTeam;
    private PickingMode pickingMode;
    private boolean casual;
    List<Player> players;

    TimerTask queueTimer;

    public Queue(String ID) {
        this.ID = ID;

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/queues/" + ID + ".yml"));

            this.playersEachTeam = Integer.parseInt(data.get("players-each-team").toString());
            this.pickingMode = PickingMode.valueOf(data.get("picking-mode").toString().toUpperCase());
            this.casual = Boolean.parseBoolean(data.get("casual").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        players = new ArrayList<>();

        QueueCache.initializeQueue(ID, this);

        Queue q = this;

        queueTimer = new TimerTask() {
            @Override
            public void run() {
                if (players.size() >= playersEachTeam * 2) {

                    List<Party> partiesInQ = new ArrayList<>();
                    List<Player> soloPlayersInQ = new ArrayList<>();

                    // CHECK FOR ALL PARTIES AND SOLO PLAYERS
                    for (Player p : players) {
                        if (PartyCache.getParty(p) != null) {
                            if (!partiesInQ.contains(PartyCache.getParty(p))) {
                                partiesInQ.add(PartyCache.getParty(p));
                            }
                        }
                        else {
                            soloPlayersInQ.add(p);
                        }
                    }

                    List<Party> partiesStillInQ = new ArrayList<>(partiesInQ);
                    // REMOVE PARTIES IF NOT ALL PARTY PLAYERS IN Q
                    if (partiesInQ.size() > 0) {
                        for (Party p : partiesInQ) {
                            for (Player player : p.getMembers()) {
                                if (!players.contains(player)) {
                                    partiesStillInQ.remove(PartyCache.getParty(player));
                                }
                            }
                        }
                    }

                    // CHECK HOW MANY STILL ABLE TO Q
                    List<Player> ableToQ = new ArrayList<>(soloPlayersInQ);
                    for (Party p : partiesStillInQ) {
                        ableToQ.addAll(p.getMembers());
                    }

                    if (ableToQ.size() >= getPlayersEachTeam() * 2) {
                        List<Player> playerList = new ArrayList<>();

                        for (Party p : partiesStillInQ) {
                            if (playerList.size() + p.getMembers().size() <= getPlayersEachTeam() * 2) {
                                playerList.addAll(p.getMembers());
                                ableToQ.removeAll(p.getMembers());
                            }
                        }

                        int tempPL = playerList.size();
                        for (int i = 0; i < getPlayersEachTeam() * 2 - tempPL; i++) {
                            playerList.add(ableToQ.get(i));

                        }

                        if (playerList.size() == getPlayersEachTeam() * 2) {
                            new Game(playerList, q).pickTeams();
                        }
                    }
                }
            }
        };

        new Timer().schedule(queueTimer, 0L, 3000L);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public static void createFile(String ID, int playersEachTeam, PickingMode pickingMode, boolean casual) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/queues/" + ID + ".yml"));
            bw.write("players-each-team: " + playersEachTeam + "\n");
            bw.write("picking-mode: " + pickingMode + "\n");
            bw.write("casual: " + casual + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String ID) {
        QueueCache.removeQueue(QueueCache.getQueue(ID));

        try {
            Files.deleteIfExists(Path.of("RankedBot/queues/" + ID + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
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
    public List<Player> getPlayers() {
        return players;
    }
}
