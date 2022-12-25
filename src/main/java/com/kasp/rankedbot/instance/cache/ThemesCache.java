package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Theme;

import java.util.HashMap;
import java.util.Map;

public class ThemesCache {

    private static HashMap<String, Theme> themes = new HashMap<>();

    public static Theme getTheme(String name) {
        return themes.get(name);
    }

    public static void addTheme(Theme theme) {
        themes.put(theme.getName(), theme);

        System.out.println("Theme " + theme.getName() + " has been loaded into memory");
    }

    public static void removeTheme(Theme theme) {
        themes.remove(theme.getName());
    }

    public static boolean containsTheme(String name) {
        return themes.containsKey(name);
    }

    public static void initializeTheme(String name, Theme theme) {
        if (!containsTheme(name))
            addTheme(theme);
    }

    public static Map<String, Theme> getThemes() {
        return themes;
    }
}
