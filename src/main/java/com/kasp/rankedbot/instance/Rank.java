package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.RankCache;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/ranks/" + ID + ".yml"));

            this.startingElo = Integer.parseInt(data.get("starting-elo").toString());
            this.endingElo = Integer.parseInt(data.get("ending-elo").toString());
            this.winElo = Integer.parseInt(data.get("win-elo").toString());
            this.loseElo = Integer.parseInt(data.get("lose-elo").toString());
            this.mvpElo = Integer.parseInt(data.get("mvp-elo").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        RankCache.initializeRank(ID, this);
    }

    public static void createFile(String ID, String startingElo, String endingElo, String winElo, String loseElo, String mvpElo) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/ranks/" + ID + ".yml"));
            bw.write("starting-elo: " + startingElo + "\n");
            bw.write("ending-elo: " + endingElo + "\n");
            bw.write("win-elo: " + winElo + "\n");
            bw.write("lose-elo: " + loseElo + "\n");
            bw.write("mvp-elo: " + mvpElo + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String ID) {
        RankCache.removeRank(RankCache.getRank(ID));

        try {
            Files.deleteIfExists(Path.of("RankedBot/ranks/" + ID + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
