package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.instance.cache.LevelCache;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Level {

    private int level;
    private int neededXP;
    private List<String> rewards;

    public Level(int level) {
        this.level = level;
        rewards = new ArrayList<>();

        if (level == 0) {
            this.neededXP = 0;
        }
        else {
            Yaml yaml = new Yaml();
            try {
                Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/levels.yml"));

                String levelData = data.get("l" + level).toString();

                if (levelData.contains(";;;")) {
                    this.neededXP = Integer.parseInt(levelData.split(";;;")[0]);

                    String rewards = levelData.split(";;;")[1];

                    if (rewards.contains(",")) {
                        this.rewards.addAll(Arrays.asList(rewards.split(",")));
                    }
                    else {
                        this.rewards.add(rewards);
                    }
                }
                else {
                    this.neededXP = Integer.parseInt(levelData);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        LevelCache.initializeLevel(level, this);
    }

    public int getNeededXP() {
        return neededXP;
    }

    public int getLevel() {
        return level;
    }

    public List<String> getRewards() {
        return rewards;
    }
}
