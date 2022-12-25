package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Game;

import java.util.HashMap;
import java.util.Map;

public class GamesCache {

    private static HashMap<Integer, Game> games = new HashMap<>();

    public static Game getGame(int number) {
        return games.get(number);
    }

    public static Game getGame(String channelID) {
        for (Game g : games.values()) {
            if (g.getChannel().getId() == channelID) {
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

    public static boolean containsGame(int number) {
        return games.containsKey(number);
    }

    public static Game initializeGame(int number, Game game) {
        if (!containsGame(number))
            addGame(game);

        return getGame(number);
    }

    public static Map<Integer, Game> getGames() {
        return games;
    }
}
