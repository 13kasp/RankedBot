package com.kasp.rankedbot.classes.cache;

import com.kasp.rankedbot.classes.rank.Rank;

import java.util.HashMap;
import java.util.Map;

public class RanksCache {

    private static HashMap<String, Rank> ranks = new HashMap<>();

    public static Rank getRank(String ID) {
        return ranks.get(ID);
    }

    public static void addRank(Rank rank) {
        ranks.put(rank.getID(), rank);

        System.out.println("Rank " + rank.getID() + " has been added to cache");
    }

    public static void removeRank(Rank rank) {
        ranks.remove(rank.getID());
    }

    public static boolean containsRank(String ID) {
        return ranks.containsKey(ID);
    }

    public static Rank rank(String ID, Rank rank) {
        if (!containsRank(ID))
            addRank(rank);

        return getRank(ID);
    }

    public static Map<String, Rank> getRanks() {
        return ranks;
    }
}
