package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.ClanLevelCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class ClanLevel {

    private int level;
    private int neededXP;

    public ClanLevel(int level) {
        this.level = level;

        if (level == 0) {
            this.neededXP = 0;
        }
        else {
            Yaml yaml = new Yaml();
            try {
                Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/clanlevels.yml"));

               neededXP = Integer.parseInt(data.get("l" + level).toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        ClanLevelCache.initializeLevel(level, this);
    }

    public int getNeededXP() {
        return neededXP;
    }

    public int getLevel() {
        return level;
    }
}
