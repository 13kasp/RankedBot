package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.GameMap;

import java.util.HashMap;
import java.util.Map;

public class MapsCache {

    private static HashMap<String, GameMap> maps = new HashMap<>();

    public static GameMap getMap(String name) {
        return maps.get(name);
    }

    public static void addMap(GameMap map) {
        maps.put(map.getName(), map);

        System.out.println("Map " + map.getName() + " has been loaded into memory");
    }

    public static void removeMap(GameMap map) {
        maps.remove(map.getName());
    }

    public static boolean containsMap(String name) {
        return maps.containsKey(name);
    }

    public static GameMap initializeMap(String name, GameMap map) {
        if (!containsMap(name))
            addMap(map);

        return getMap(name);
    }

    public static Map<String, GameMap> getMaps() {
        return maps;
    }
}
