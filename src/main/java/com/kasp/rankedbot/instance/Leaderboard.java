package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.instance.cache.PlayerCache;

import java.util.*;

public class Leaderboard {

    public static List<String> getLeaderboard(Statistic statistic) {
        Map<Player, Double> unsortedMap = new HashMap<>();

        for (Player p : PlayerCache.getPlayers().values()) {

            switch (statistic) {
                case PEAKELO:
                    unsortedMap.put(p, (double) p.getPeakElo());
                    break;
                case WINS:
                    unsortedMap.put(p, (double) p.getWins());
                    break;
                case LOSSES:
                    unsortedMap.put(p, (double) p.getLosses());
                    break;
                case WINSTREAK:
                    unsortedMap.put(p, (double) p.getWinStreak());
                    break;
                case LOSSSTREAK:
                    unsortedMap.put(p, (double) p.getLossStreak());
                    break;
                case HIGHESTWS:
                    unsortedMap.put(p, (double) p.getHighestWS());
                    break;
                case HIGHESTLS:
                    unsortedMap.put(p, (double) p.getHighestLS());
                    break;
                case MVP:
                    unsortedMap.put(p, (double) p.getMvp());
                    break;
                case KILLS:
                    unsortedMap.put(p, (double) p.getKills());
                    break;
                case DEATHS:
                    unsortedMap.put(p, (double) p.getDeaths());
                    break;
                case STRIKES:
                    unsortedMap.put(p, (double) p.getStrikes());
                    break;
                case SCORED:
                    unsortedMap.put(p, (double) p.getScored());
                    break;
                case GOLD:
                    unsortedMap.put(p, (double) p.getGold());
                    break;
                case LEVEL:
                    unsortedMap.put(p, (double) p.getLevel());
                    break;
                case XP:
                    unsortedMap.put(p, (double) p.getXp());
                    break;
                case GAMES:
                    unsortedMap.put(p, (double) (p.getWins() + p.getLosses()));
                    break;
                case WLR:
                    double templosses;
                    if (p.getLosses() == 0)
                        templosses = 1.0;
                    else
                        templosses = p.getLosses();

                    unsortedMap.put(p, p.getWins() / templosses);
                    break;
                default:
                    unsortedMap.put(p, (double) p.getElo());
            }
        }

        List<String> lb = new ArrayList<>();

        List<Map.Entry<Player, Double>> list = new LinkedList<>(unsortedMap.entrySet());
        list.sort(java.util.Map.Entry.comparingByValue());
        Collections.reverse(list);

        Map<Player, Double> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<Player, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Player, Double> entry : sortedMap.entrySet()) {
            lb.add(entry.getKey().getID() + "=" + entry.getValue());
        }

        return lb;
    }
}
