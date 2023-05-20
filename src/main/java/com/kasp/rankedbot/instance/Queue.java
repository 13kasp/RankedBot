package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.database.SQLite;
import com.kasp.rankedbot.instance.cache.PartyCache;
import com.kasp.rankedbot.instance.cache.QueueCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Queue {

    private String ID;
    private int playersEachTeam;
    private PickingMode pickingMode;
    private boolean casual;
    private List<Player> players;
    private double eloMultiplier;

    TimerTask queueTimer;

    public Queue(String ID) {
        this.ID = ID;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM queues WHERE discordID='" + ID + "';");

        try {
            this.playersEachTeam = resultSet.getInt(3);
            this.pickingMode = PickingMode.valueOf(resultSet.getString(4).toUpperCase());
            this.casual = Boolean.parseBoolean(resultSet.getString(5));
            this.eloMultiplier = resultSet.getDouble(6);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        players = new ArrayList<>();

        QueueCache.initializeQueue(ID, this);

        Queue q = this;

        queueTimer = new TimerTask() {
            @Override
            public void run() {
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Timer().schedule(queueTimer, 0L, 3000L);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public static void delete(String ID) {
        QueueCache.removeQueue(QueueCache.getQueue(ID));

        SQLite.updateData("DELETE FROM queues WHERE discordID='" + ID + "';");
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
    public double getEloMultiplier() {
        return eloMultiplier;
    }
}
