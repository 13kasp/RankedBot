package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.MapsCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class GameMap {

    private String name;
    private int height;
    private String team1;
    private String team2;

    public GameMap(String name) {
        this.name = name;

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/maps/" + name));

            this.height = Integer.parseInt(data.get("height-limit").toString());
            this.team1 = data.get("team-1").toString();
            this.team2 = data.get("team-2").toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        MapsCache.initializeMap(name, this);
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
