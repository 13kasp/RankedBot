package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.MapsCache;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/maps/" + name + ".yml"));

            this.height = Integer.parseInt(data.get("height-limit").toString());
            this.team1 = data.get("team-1").toString();
            this.team2 = data.get("team-2").toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        MapsCache.initializeMap(name, this);
    }

    public static void createFile(String name, String height, String team1, String team2) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/maps/" + name + ".yml"));
            bw.write("height-limit: " + height + "\n");
            bw.write("team-1: " + team1 + "\n");
            bw.write("team-2: " + team2 + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String name) {
        MapsCache.removeMap(MapsCache.getMap(name));

        try {
            Files.deleteIfExists(Path.of("RankedBot/maps/" + name + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
