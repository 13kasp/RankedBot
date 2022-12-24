package com.kasp.rankedbot.classes.rank;

public class Rank {

    private String ID;
    private int startingElo;
    private int endingElo;
    private int winElo;
    private int loseElo;
    private int mvpElo;

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
