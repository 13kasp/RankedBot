package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.database.SQLite;
import com.kasp.rankedbot.instance.cache.RankCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Rank {

    private String ID;
    private int startingElo;
    private int endingElo;
    private int winElo;
    private int loseElo;
    private int mvpElo;

    public Rank(String ID) {
        this.ID = ID;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM ranks WHERE discordID='" + ID + "';");

        try {
            this.startingElo = resultSet.getInt(3);
            this.endingElo = resultSet.getInt(4);
            this.winElo = resultSet.getInt(5);
            this.loseElo = resultSet.getInt(6);
            this.mvpElo = resultSet.getInt(7);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        RankCache.initializeRank(ID, this);
    }

    public static void delete(String ID) {
        RankCache.removeRank(RankCache.getRank(ID));

        SQLite.updateData("DELETE FROM ranks WHERE discordID='" + ID + "';");
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public int getStartingElo() {
        return startingElo;
    }
    public void setStartingElo(int startingElo) {
        this.startingElo = startingElo;
    }
    public int getEndingElo() {
        return endingElo;
    }
    public void setEndingElo(int endingElo) {
        this.endingElo = endingElo;
    }
    public int getWinElo() {
        return winElo;
    }
    public void setWinElo(int winElo) {
        this.winElo = winElo;
    }
    public int getLoseElo() {
        return loseElo;
    }
    public void setLoseElo(int loseElo) {
        this.loseElo = loseElo;
    }
    public int getMvpElo() {
        return mvpElo;
    }
    public void setMvpElo(int mvpElo) {
        this.mvpElo = mvpElo;
    }
}
