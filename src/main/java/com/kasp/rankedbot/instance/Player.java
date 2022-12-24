package com.kasp.rankedbot.classes.player;

import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.classes.cache.RanksCache;
import com.kasp.rankedbot.classes.cache.ThemesCache;
import com.kasp.rankedbot.classes.rank.Rank;
import com.kasp.rankedbot.classes.theme.Theme;
import com.kasp.rankedbot.config.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class Player {

    private String ID;
    private String ign;
    private int elo;
    private int peakElo;
    private int wins;
    private int losses;
    private int winStreak;
    private int lossStreak;
    private int highestWS;
    private int highestLS;
    private int mvp;
    private int kills;
    private int deaths;
    private int strikes;
    private int scored;
    private int gold;
    private int level;
    private int xp;
    private Theme theme;
    private ArrayList<Theme> ownedThemes;
    private boolean isBanned;
    private String bannedTill;

    public Player(String ID, String ign) {
        this.ID = ID;

        Yaml yaml = new Yaml();
        Map<String, Object> data = null;

        if (ign == null) {
            try {
                data = yaml.load(new FileInputStream("RankedBot/players/" + ID));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            writeFile(true);
        }

        this.ign = data.get("name").toString();
        this.elo = Integer.parseInt(data.get("elo").toString());
        this.peakElo = Integer.parseInt(data.get("peak-elo").toString());
        this.wins = Integer.parseInt(data.get("wins").toString());
        this.losses = Integer.parseInt(data.get("losses").toString());
        this.winStreak = Integer.parseInt(data.get("win-streak").toString());
        this.lossStreak = Integer.parseInt(data.get("loss-streak").toString());
        this.highestWS = Integer.parseInt(data.get("highest-ws").toString());
        this.highestLS = Integer.parseInt(data.get("highest-ls").toString());
        this.mvp = Integer.parseInt(data.get("mvp").toString());
        this.kills = Integer.parseInt(data.get("kills").toString());
        this.deaths = Integer.parseInt(data.get("deaths").toString());
        this.strikes = Integer.parseInt(data.get("strikes").toString());
        this.scored = Integer.parseInt(data.get("scored").toString());
        this.gold = Integer.parseInt(data.get("gold").toString());
        this.level = Integer.parseInt(data.get("level").toString());
        this.xp = Integer.parseInt(data.get("xp").toString());
        this.theme = ThemesCache.getTheme(data.get("theme").toString());

        ownedThemes = new ArrayList<>();

        String[] themes = data.get("owned-themes").toString().split(",");
        for (String s : themes) {
            ownedThemes.add(ThemesCache.getTheme(s));
        }

        this.isBanned = Boolean.parseBoolean(data.get("is-banned").toString());
    }

    public void fix() {
        Guild guild = RankedBot.getGuild();
        Member member = guild.getMemberById(ID);

        ArrayList<Role> rolestoremove = new ArrayList<>();
        ArrayList<Role> rolestoadd = new ArrayList<>();

        rolestoadd.add(guild.getRoleById(Config.getValue("registered-role")));

        Rank rank = getRank();
        rolestoadd.add(guild.getRoleById(rank.getID()));

        for (Rank r : RanksCache.getRanks().values()) {
            if (rank != r) {
                rolestoremove.add(guild.getRoleById(r.getID()));
            }
        }

        guild.modifyMemberRoles(member, rolestoadd, rolestoremove).queue();
        member.modifyNickname(Config.getValue("elo-formatting").replaceAll("%elo%", elo + "") + ign).queue();
    }

    public Rank getRank() {
        for (Rank r : RanksCache.getRanks().values()) {
            if (elo >= r.getStartingElo() && elo <= r.getEndingElo()) {
                return r;
            }
        }

        return null;
    }

    public static boolean isRegistered(String ID) {
        return new File("RankedBot/players/" + ID).exists();
    }

    public void writeFile(boolean isRegistering) {
        try {
            if (isRegistering) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/players/" + ID + ".yml"));

                bw.write("name: " + ign + "\n");
                bw.write("elo: " + Config.getValue("starting-elo") + "\n");
                bw.write("peak-elo: " + Config.getValue("starting-elo") + "\n");
                bw.write("wins: 0\n");
                bw.write("losses: 0\n");
                bw.write("win-streak: 0\n");
                bw.write("loss-streak: 0\n");
                bw.write("highest-ws: 0\n");
                bw.write("highest-ls: 0\n");
                bw.write("mvp: 0\n");
                bw.write("kills: 0\n");
                bw.write("deaths: 0\n");
                bw.write("strikes: 0\n");
                bw.write("scored: 0\n");
                bw.write("gold: 0\n");
                bw.write("level: 0\n");
                bw.write("xp: 0\n");
                bw.write("theme: default\n");
                bw.write("owned-themes: default\n");
                bw.write("is-banned: false\n");
                bw.write("banned-till:\n");
                bw.close();
            }
            else {
                BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/players/" + ID + ".yml"));

                bw.write("name: " + ign + "\n");
                bw.write("elo: " + elo + "\n");
                bw.write("peak-elo: " + peakElo + "\n");
                bw.write("wins: " + wins + "\n");
                bw.write("losses: " + losses + "\n");
                bw.write("win-streak: " + winStreak + "\n");
                bw.write("loss-streak: " + lossStreak + "\n");
                bw.write("highest-ws: " + highestWS + "\n");
                bw.write("highest-ls: " + highestLS + "\n");
                bw.write("mvp: " + mvp + "\n");
                bw.write("kills: " + kills + "\n");
                bw.write("deaths: " + deaths + "\n");
                bw.write("strikes: " + strikes + "\n");
                bw.write("scored: " + scored + "\n");
                bw.write("gold: " + gold + "\n");
                bw.write("level: " + level + "\n");
                bw.write("xp: " + xp + "\n");
                bw.write("theme: " + theme.getName() + "\n");

                StringBuilder themes = new StringBuilder();
                for (Theme t : ownedThemes) {
                    themes.append(t.getName());
                    if (ownedThemes.indexOf(t) != ownedThemes.size() - 1) {
                        themes.append(",");
                    }
                }

                bw.write("owned-themes: " + themes + "\n");
                bw.write("is-banned: " + isBanned + "\n");
                bw.write("banned-till: " + bannedTill + "\n");
                bw.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getIgn() {
        return ign;
    }

    public void setIgn(String ign) {
        this.ign = ign;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getPeakElo() {
        return peakElo;
    }

    public void setPeakElo(int peakElo) {
        this.peakElo = peakElo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public int getLossStreak() {
        return lossStreak;
    }

    public void setLossStreak(int lossStreak) {
        this.lossStreak = lossStreak;
    }

    public int getHighestWS() {
        return highestWS;
    }

    public void setHighestWS(int highestWS) {
        this.highestWS = highestWS;
    }

    public int getHighestLS() {
        return highestLS;
    }

    public void setHighestLS(int highestLS) {
        this.highestLS = highestLS;
    }

    public int getMvp() {
        return mvp;
    }

    public void setMvp(int mvp) {
        this.mvp = mvp;
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public int getScored() {
        return scored;
    }

    public void setScored(int scored) {
        this.scored = scored;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public ArrayList<Theme> getOwnedThemes() {
        return ownedThemes;
    }

    public void setOwnedThemes(ArrayList<Theme> ownedThemes) {
        this.ownedThemes = ownedThemes;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getBannedTill() {
        return bannedTill;
    }

    public void setBannedTill(String bannedTill) {
        this.bannedTill = bannedTill;
    }
}
