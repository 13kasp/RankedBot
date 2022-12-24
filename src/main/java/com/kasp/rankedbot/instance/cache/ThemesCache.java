package com.kasp.rankedbot.classes.cache;

import com.kasp.rankedbot.classes.theme.Theme;

import java.util.HashMap;
import java.util.Map;

public class ThemesCache {

    private static HashMap<String, Theme> themes = new HashMap<>();

    public static Theme getTheme(String name) {
        return themes.get(name);
    }

    public static void addTheme(Theme theme) {
        themes.put(theme.getName(), theme);

        System.out.println("Theme " + theme.getName() + " has been added to cache");
    }

    public static void removeTheme(Theme theme) {
        themes.remove(theme.getName());
    }

    public static boolean containsTheme(String name) {
        return themes.containsKey(name);
    }

    public static void initializeTheme(String ID, Theme theme) {
        if (!containsTheme(ID))
            addTheme(theme);
    }

    public static Map<String, Theme> getThemes() {
        return themes;
    }
}
