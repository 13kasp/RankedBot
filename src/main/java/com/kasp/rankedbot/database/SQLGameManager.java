package com.kasp.rankedbot.database;

import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.GameCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameManager {

    public static void createGame(Game g) {
        String team1 = "";
        String team2 = "";

        SQLite.updateData("INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2)" +
                " VALUES(" + g.getNumber() + "," +
                "'" + g.getState().toString().toUpperCase() + "'," +
                "'" + g.isCasual() + "'," +
                "'" + g.getMap().getName() + "'," +
                "'" + g.getChannelID() + "'," +
                "'" + g.getVC1ID() + "'," +
                "'" + g.getVC2ID() + "'," +
                "'" + g.getQueue().getID() + "'," +
                "'" + team1 + "'," +
                "'" + team2 + "');");
    }

    public static void updateGame(Game g) {
        String team1 = "";
        String team2 = "";

        for (Player p : g.getTeam1()) {
            team1 += p.getID();
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1 += ",";
            }
        }

        for (Player p : g.getTeam2()) {
            team2 += p.getID();
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2 += ",";
            }
        }

        SQLite.updateData("INSERT INTO games(number, state, casual, map, channelID, vc1ID, vc2ID, queueID, team1, team2)" +
                " VALUES(" + g.getNumber() + "," +
                "'" + g.getState().toString().toUpperCase() + "'," +
                "'" + g.isCasual() + "'," +
                "'" + g.getMap().getName() + "'," +
                "'" + g.getChannelID() + "'," +
                "'" + g.getVC1ID() + "'," +
                "'" + g.getVC2ID() + "'," +
                "'" + g.getQueue().getID() + "'," +
                "'" + team1 + "'," +
                "'" + team2 + "');");
    }

    public static int getGameSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(_ID) FROM games");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void updateState(int number) {
        SQLite.updateData("UPDATE games SET state = '" + GameCache.getGame(number).getState().toString().toUpperCase() + "' WHERE number=" + number + ";");
    }

    public static void updateMvp(int number) {
        SQLite.updateData("UPDATE games SET mvp = '" + GameCache.getGame(number).getMvp().getIgn() + "' WHERE number=" + number + ";");
    }

    public static void updateScoredBy(int number) {
        SQLite.updateData("UPDATE games SET scoredBy = '" + GameCache.getGame(number).getScoredBy().getId() + "' WHERE number=" + number + ";");
    }

    public static void updateEloGain(int number) {
        Game g = GameCache.getGame(number);

        String team1 = "";
        String team2 = "";

        for (Player p : g.getTeam1()) {
            team1 += p.getID() + "=" + g.getEloGain().get(p);
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1 += ",";
            }
        }

        for (Player p : g.getTeam2()) {
            team2 += p.getID() + "=" + g.getEloGain().get(p);
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2 += ",";
            }
        }

        SQLite.updateData("UPDATE games SET team1 = '" + team1 + "' WHERE number=" + number + ";");
        SQLite.updateData("UPDATE games SET team2 = '" + team2 + "' WHERE number=" + number + ";");
    }

    public static void removeEloGain(int number) {
        Game g = GameCache.getGame(number);

        String team1 = "";
        String team2 = "";

        for (Player p : g.getTeam1()) {
            team1 += p.getID();
            if (g.getTeam1().indexOf(p)+1 < g.getTeam1().size()) {
                team1 += ",";
            }
        }

        for (Player p : g.getTeam2()) {
            team2 += p.getID();
            if (g.getTeam2().indexOf(p)+1 < g.getTeam2().size()) {
                team2 += ",";
            }
        }

        SQLite.updateData("UPDATE games SET team1 = '" + team1 + "' WHERE number=" + number + ";");
        SQLite.updateData("UPDATE games SET team2 = '" + team2 + "' WHERE number=" + number + ";");
    }
}
