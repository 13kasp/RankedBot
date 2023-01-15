package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Level;

import java.util.HashMap;
import java.util.Map;

public class LevelCache {

    private static Map<Integer, Level> levels = new HashMap<>();

    public static Level getLevel(int level) {
        return levels.get(level);
    }

    public static void addLevel(Level level) {
        levels.put(level.getLevel(), level);

        System.out.println("Level " + level.getLevel() + " has been loaded into memory");
    }

    public static void removeLevel(int level) {
        levels.remove(level);
    }

    public static boolean containsLevel(int level) {
        return levels.containsKey(level);
    }

    public static Level initializeLevel(int level, Level lvlobj) {
        if (!containsLevel(level))
            addLevel(lvlobj);

        return getLevel(level);
    }

    public static Map<Integer, Level> getLevels() {
        return levels;
    }
}
