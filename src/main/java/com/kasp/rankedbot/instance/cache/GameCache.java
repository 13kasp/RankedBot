package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Game;

import java.util.HashMap;
import java.util.Map;

public class GameCache {

    public static HashMap<Integer, Game> games = new HashMap<>();

    public static Game getGame(int number) {
        return games.get(number);
    }

    public static Game getGame(String channelID) {

        for (Game g : games.values()) {
            if (g.getChannel().getId().equals(channelID)) {
                return g;
            }
        }

        return null;
    }

    public static void addGame(Game game) {
        games.put(game.getNumber(), game);

        System.out.println("Game " + game.getNumber() + " has been loaded into memory");
    }

    public static void removeGame(Game game) {
        games.remove(game.getNumber());
    }

    public static boolean containsGame(Game game) {
        return games.containsValue(game);
    }

    public static void initializeGame(Game game) {
        if (!containsGame(game))
            addGame(game);
    }

    public static Map<Integer, Game> getGames() {
        return games;
    }
}
