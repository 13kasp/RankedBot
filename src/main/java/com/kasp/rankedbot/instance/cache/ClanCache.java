package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Player;

import java.util.HashMap;
import java.util.Map;

public class ClanCache {

    private static Map<String, Clan> clans = new HashMap<>();

    public static Clan getClan(String name) {
        return clans.get(name);
    }

    public static Clan getClan(Player player) {
        for (Clan c : clans.values()) {
            if (c.getMembers().contains(player)) {
                return c;
            }
        }

        return null;
    }

    public static void addClan(Clan clan) {
        clans.put(clan.getName(), clan);

        System.out.println("Clan " + clan.getName() + " has been loaded into memory");
    }

    public static void removeClan(String name) {
        clans.remove(name);
    }

    public static boolean containsClan(String name) {
        return clans.containsKey(name);
    }

    public static Clan initializeClan(String name, Clan clan) {
        if (!containsClan(name))
            addClan(clan);

        return getClan(name);
    }

    public static Map<String, Clan> getClans() {
        return clans;
    }
}
