package com.kasp.rankedbot.database;

import com.kasp.rankedbot.instance.cache.ClanCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLClanManager {

    public static void createClan(String name, String leader) {
        SQLite.updateData("INSERT INTO clans(name, leader, members, reputation, xp, level, wins, losses, private, eloJoinReq, description)" +
                " VALUES('" + name + "'," +
                "'" + leader + "'," +
                "'" + leader + "'," +
                0 + "," +
                0 + "," +
                0 + "," +
                0 + "," +
                0 + "," +
                "'" + true + "'," +
                0 + "," +
                "'A newly created clan');");
    }

    public static int getClanSize() {
        ResultSet resultSet = SQLite.queryData("SELECT COUNT(name) FROM clans");
        try {
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void updateXP(String name) {
        SQLite.updateData("UPDATE clans SET xp = '" + ClanCache.getClan(name).getXp() + "' WHERE name='" + name + "';");
    }

    public static void updateLevel(String name) {
        SQLite.updateData("UPDATE clans SET level = '" + ClanCache.getClan(name).getLevel().getLevel() + "' WHERE name='" + name + "';");
    }

    public static void updatePrivate(String name) {
        SQLite.updateData("UPDATE clans SET private = '" + ClanCache.getClan(name).isPrivate() + "' WHERE name='" + name + "';");
    }

    public static void updateEloJoinReq(String name) {
        SQLite.updateData("UPDATE clans SET eloJoinReq = '" + ClanCache.getClan(name).getEloJoinReq() + "' WHERE name='" + name + "';");
    }

    public static void updateDescription(String name) {
        SQLite.updateData("UPDATE clans SET description = '" + ClanCache.getClan(name).getDescription() + "' WHERE name='" + name + "';");
    }
}
