package com.kasp.rankedbot.database;

import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Theme;
import com.kasp.rankedbot.instance.cache.PlayerCache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class SQLPlayerManager {

    public static void createPlayer(String ID, String ign) {
        SQLite.updateData("INSERT INTO players(discordID, ign, elo, peakElo, wins, losses, winStreak, lossStreak, highestWS, highestLS," +
                "mvp, kills, deaths, strikes, scored, gold, level, xp, theme, ownedThemes, isBanned, bannedTill)" +
                " VALUES('" + ID + "'," +
                "'" + ign + "'," +
                Config.getValue("starting-elo") + "," + // elo
                Config.getValue("starting-elo") + "," + // peak elo
                "0," + // wins
                "0," + // losses
                "0," + // ws
                "0," + // ls
                "0," + // highest ws
                "0," + // highest ls
                "0," + // mvp
                "0," + // kills
                "0," + // deaths
                "0," + // strikes
                "0," + // scored
                "0," + // gold
                "0," + // level
                "0," + // xp
                "'default'," + // theme
                "'default'," + // owned themes
                "'false'," + // is banned
                "'');"); // banned till
    }

    public static int getPlayerSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(discordID) FROM players");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean isRegistered(String ID) {
        ResultSet resultSet = SQLite.queryData("SELECT EXISTS(SELECT 1 FROM players WHERE discordID='" + ID + "');");

        try {
            if (resultSet.getInt(1) == 1)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void updateIgn(String ID) {
        SQLite.updateData("UPDATE players SET ign = '" + PlayerCache.getPlayer(ID).getIgn() + "' WHERE discordID='" + ID + "';");
    }

    public static void updateElo(String ID) {
        SQLite.updateData("UPDATE players SET elo = " + PlayerCache.getPlayer(ID).getElo() + " WHERE discordID='" + ID + "';");
    }

    public static void updatePeakElo(String ID) {
        SQLite.updateData("UPDATE players SET peakElo = " + PlayerCache.getPlayer(ID).getPeakElo() + " WHERE discordID='" + ID + "';");
    }

    public static void updateWins(String ID) {
        SQLite.updateData("UPDATE players SET wins = " + PlayerCache.getPlayer(ID).getWins() + " WHERE discordID='" + ID + "';");
    }

    public static void updateLosses(String ID) {
        SQLite.updateData("UPDATE players SET losses = " + PlayerCache.getPlayer(ID).getLosses() + " WHERE discordID='" + ID + "';");
    }

    public static void updateWinStreak(String ID) {
        SQLite.updateData("UPDATE players SET winStreak = " + PlayerCache.getPlayer(ID).getWinStreak() + " WHERE discordID='" + ID + "';");
    }

    public static void updateLossStreak(String ID) {
        SQLite.updateData("UPDATE players SET lossStreak = " + PlayerCache.getPlayer(ID).getLossStreak() + " WHERE discordID='" + ID + "';");
    }

    public static void updateHighestWS(String ID) {
        SQLite.updateData("UPDATE players SET highestWS = " + PlayerCache.getPlayer(ID).getHighestWS() + " WHERE discordID='" + ID + "';");
    }

    public static void updateHighestLS(String ID) {
        SQLite.updateData("UPDATE players SET highestLS = " + PlayerCache.getPlayer(ID).getHighestLS() + " WHERE discordID='" + ID + "';");
    }

    public static void updateMvp(String ID) {
        SQLite.updateData("UPDATE players SET mvp = " + PlayerCache.getPlayer(ID).getMvp() + " WHERE discordID='" + ID + "';");
    }

    public static void updateKills(String ID) {
        SQLite.updateData("UPDATE players SET kills = " + PlayerCache.getPlayer(ID).getKills() + " WHERE discordID='" + ID + "';");
    }

    public static void updateDeaths(String ID) {
        SQLite.updateData("UPDATE players SET deaths = " + PlayerCache.getPlayer(ID).getDeaths() + " WHERE discordID='" + ID + "';");
    }

    public static void updateStrikes(String ID) {
        SQLite.updateData("UPDATE players SET strikes = " + PlayerCache.getPlayer(ID).getStrikes() + " WHERE discordID='" + ID + "';");
    }

    public static void updateScored(String ID) {
        SQLite.updateData("UPDATE players SET scored = " + PlayerCache.getPlayer(ID).getScored() + " WHERE discordID='" + ID + "';");
    }

    public static void updateGold(String ID) {
        SQLite.updateData("UPDATE players SET gold = " + PlayerCache.getPlayer(ID).getGold() + " WHERE discordID='" + ID + "';");
    }

    public static void updateLevel(String ID) {
        SQLite.updateData("UPDATE players SET level = " + PlayerCache.getPlayer(ID).getLevel().getLevel() + " WHERE discordID='" + ID + "';");
    }

    public static void updateXP(String ID) {
        SQLite.updateData("UPDATE players SET xp = " + PlayerCache.getPlayer(ID).getXp() + " WHERE discordID='" + ID + "';");
    }

    public static void updateTheme(String ID) {
        SQLite.updateData("UPDATE players SET theme = '" + PlayerCache.getPlayer(ID).getTheme().getName() + "' WHERE discordID='" + ID + "';");
    }

    public static void updateOwnedThemes(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        StringBuilder themes = new StringBuilder();
        for (Theme t : player.getOwnedThemes()) {
            themes.append(t.getName());
            if (player.getOwnedThemes().indexOf(t) != player.getOwnedThemes().size() - 1) {
                themes.append(",");
            }
        }

        SQLite.updateData("UPDATE players SET ownedThemes = '" + themes + "' WHERE discordID='" + ID + "';");
    }

    public static void updateIsBanned(String ID) {
        SQLite.updateData("UPDATE players SET isBanned = '" + PlayerCache.getPlayer(ID).isBanned() + "' WHERE discordID='" + ID + "';");
    }

    public static void updateBannedTill(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        if (player.isBanned()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String bannedTill = formatter.format(player.getBannedTill());

            SQLite.updateData("UPDATE players SET bannedTill = '" + bannedTill + "' WHERE discordID='" + ID + "';");
        }
        else {
            SQLite.updateData("UPDATE players SET bannedTill = '' WHERE discordID='" + ID + "';");
        }
    }

    public static void updateBanReason(String ID) {
        Player player = PlayerCache.getPlayer(ID);

        if (player.isBanned()) {
            SQLite.updateData("UPDATE players SET banReason = '" + PlayerCache.getPlayer(ID).getBanReason() + "' WHERE discordID='" + ID + "';");
        }
        else {
            SQLite.updateData("UPDATE players SET banReason = '' WHERE discordID='" + ID + "';");
        }
    }
}
