package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCache {

    private static HashMap<String, Player> players = new HashMap<>();

    public static Player getPlayer(String ID) {
        return players.get(ID);
    }

    public static void addPlayer(Player player) {
        players.put(player.getID(), player);

        System.out.println("Player " + player.getIgn() + " (" + player.getID() + ")" + " has been loaded into memory");
    }

    public static void removePlayer(Player player) {
        players.remove(player.getID());
    }

    public static boolean containsPlayer(Player player) {
        return players.containsValue(player);
    }

    public static void initializePlayer(Player player) {
        if (!containsPlayer(player))
            addPlayer(player);
    }

    public static Map<String, Player> getPlayers() {
        return players;
    }
}
