package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.ThemesCache;

public class Theme {

    private String name;

    public Theme(String name) {
        this.name = name;

        ThemesCache.initializeTheme(name, this);
    }

    public String getName() {
        return name;
    }
}
