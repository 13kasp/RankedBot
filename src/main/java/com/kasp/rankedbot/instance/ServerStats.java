package com.kasp.rankedbot.instance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ServerStats {

    private static int gamesPlayed;

    public static int getGamesPlayed() {
        return gamesPlayed;
    }
    public static void setGamesPlayed(int gamesPlayed) {
        ServerStats.gamesPlayed = gamesPlayed;
    }
    public static void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/serverstats.yml"));

            bw.write("games-played: " + gamesPlayed);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
