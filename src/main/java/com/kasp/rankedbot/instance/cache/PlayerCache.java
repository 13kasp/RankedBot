package com.kasp.rankedbot.classes.cache;

import com.kasp.rankedbot.classes.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCache {

    private static HashMap<String, Player> players = new HashMap<>();

    public static Player getPlayer(String ID) {
        return players.get(ID);
    }

    public static void addPlayer(Player player) {
        players.put(player.getID(), player);

        System.out.println("Player " + player.getIgn() + " (" + player.getID() + ")" + " has been added to cache");
    }

    public static void removePlayer(Player player) {
        players.remove(player.getID());
    }

    public static boolean containsPlayer(String ID) {
        return players.containsKey(ID);
    }

    public static void initializePlayer(String ID, Player player) {
        if (!containsPlayer(ID))
            addPlayer(player);
    }

    public static Map<String, Player> getPlayers() {
        return players;
    }
}
