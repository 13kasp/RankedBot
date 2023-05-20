package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.database.SQLite;
import com.kasp.rankedbot.instance.cache.MapCache;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameMap {

    private String name;
    private int height;
    private String team1;
    private String team2;

    public GameMap(String name) {
        this.name = name;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM maps WHERE name='" + name + "';");

        try {
            this.height = resultSet.getInt(3);
            this.team1 = resultSet.getString(4);
            this.team2 = resultSet.getString(5);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MapCache.initializeMap(name, this);
    }

    public static void delete(String name) {
        MapCache.removeMap(MapCache.getMap(name));

        SQLite.updateData("DELETE FROM maps WHERE name='" + name + "';");
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getTeam1() {
        return team1;
    }
    public void setTeam1(String team1) {
        this.team1 = team1;
    }
    public String getTeam2() {
        return team2;
    }
    public void setTeam2(String team2) {
        this.team2 = team2;
    }
}
