package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.RanksCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Rank {

    private String ID;
    private int startingElo;
    private int endingElo;
    private int winElo;
    private int loseElo;
    private int mvpElo;

    public Rank(String ID) {
        this.ID = ID;

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/ranks/" + ID));

            this.startingElo = Integer.parseInt(data.get("starting-elo").toString());
            this.endingElo = Integer.parseInt(data.get("ending-elo").toString());
            this.winElo = Integer.parseInt(data.get("win-elo").toString());
            this.loseElo = Integer.parseInt(data.get("lose-elo").toString());
            this.mvpElo = Integer.parseInt(data.get("mvp-elo").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        RanksCache.initializeRank(ID, this);
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
