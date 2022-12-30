package com.kasp.rankedbot.instance.cache;

import com.kasp.rankedbot.instance.Party;
import com.kasp.rankedbot.instance.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyCache {

    private static List<Party> parties = new ArrayList<>();

    public static Party getParty(Player player) {
        for (Party p : parties) {
            if (p.getMembers().contains(player)) {
                return p;
            }
        }

        return null;
    }

    public static void addParty(Party party) {
        parties.add(party);

        System.out.println("Party created by " + party.getLeader() + " has been loaded into memory");
    }

    public static void removeParty(Party party) {
        parties.remove(party);
    }

    public static boolean containsParty(Party party) {
        return parties.contains(party);
    }

    public static void initializeParty(Party party) {
        if (!containsParty(party))
            addParty(party);
    }

    public static List<Party> getParties() {
        return parties;
    }
}
