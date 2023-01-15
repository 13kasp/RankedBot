package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.cache.*;
import com.kasp.rankedbot.instance.embed.Embed;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private Level level;
    private int xp;
    private Theme theme;
    private ArrayList<Theme> ownedThemes;
    private boolean isBanned;
    private LocalDateTime bannedTill;
    private String banReason;

    public Player(String ID) {
        this.ID = ID;

        try {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/players/" + ID + ".yml"));

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
            this.level = LevelCache.getLevel(Integer.parseInt(data.get("level").toString()));
            this.xp = Integer.parseInt(data.get("xp").toString());
            this.theme = ThemeCache.getTheme(data.get("theme").toString());

            ownedThemes = new ArrayList<>();

            String[] themes = data.get("owned-themes").toString().split(",");
            for (String s : themes) {
                ownedThemes.add(ThemeCache.getTheme(s));
            }

            this.isBanned = Boolean.parseBoolean(data.get("is-banned").toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PlayerCache.initializePlayer(this);
    }

    public void fix() {
        Guild guild = RankedBot.getGuild();
        Member member = guild.getMemberById(ID);

        ArrayList<Role> rolestoremove = new ArrayList<>();
        ArrayList<Role> rolestoadd = new ArrayList<>();

        rolestoadd.add(guild.getRoleById(Config.getValue("registered-role")));

        Rank rank = getRank();
        rolestoadd.add(guild.getRoleById(rank.getID()));

        for (Rank r : RankCache.getRanks().values()) {
            if (rank != r) {
                rolestoremove.add(guild.getRoleById(r.getID()));
            }
        }

        if (isBanned) {
            if (getBannedTill().isBefore(LocalDateTime.now())) {
                rolestoremove.add(guild.getRoleById(Config.getValue("banned-role")));
            }
            else {
                rolestoadd.add(guild.getRoleById(Config.getValue("banned-role")));
            }
        }
        else {
            rolestoremove.add(guild.getRoleById(Config.getValue("banned-role")));
        }

        guild.modifyMemberRoles(member, rolestoadd, rolestoremove).queue();
        member.modifyNickname(Config.getValue("elo-formatting").replaceAll("%elo%", elo + "") + ign).queue();
    }

    public void wipe() {
        elo = Integer.parseInt(Config.getValue("starting-elo"));
        peakElo = Integer.parseInt(Config.getValue("starting-elo"));
        wins = 0;
        losses = 0;
        winStreak = 0;
        lossStreak = 0;
        highestWS = 0;
        highestLS = 0;
        mvp = 0;
        kills = 0;
        deaths = 0;
    }

    public void win() {
        if (Boolean.parseBoolean(Config.getValue("levels-enabled"))) {
            updateXP(Integer.parseInt(Config.getValue("play-xp")));
            updateXP(Integer.parseInt(Config.getValue("win-xp")));
        }

        wins++;
        winStreak++;
        elo += getRank().getWinElo();

        if (peakElo < elo) {
            peakElo = elo;
        }

        if (lossStreak > 0) {
            lossStreak = 0;
        }

        if (highestWS < winStreak) {
            highestWS = winStreak;
        }
    }

    public void lose() {
        if (Boolean.parseBoolean(Config.getValue("levels-enabled"))) {
            updateXP(Integer.parseInt(Config.getValue("play-xp")));
        }

        losses++;
        lossStreak++;

        if (elo - getRank().getLoseElo() > 0) {
            elo -= getRank().getLoseElo();
        }
        else {
            elo = 0;
        }

        if (winStreak > 0) {
            winStreak = 0;
        }

        if (highestLS < lossStreak) {
            highestLS = lossStreak;
        }
    }

    public void updateXP(int addXp) {
        xp+=addXp;

        for (int i = level.getLevel()+1; i < LevelCache.getLevels().size(); i++) {
            if (xp >= LevelCache.getLevel(i).getNeededXP()) {
                level = LevelCache.getLevel(i);

                for (String s : LevelCache.getLevel(i).getRewards()) {
                    if (s.startsWith("GOLD")) {
                        gold+=Integer.parseInt(s.split("=")[1]);
                    }
                }

                Embed embed = new Embed(EmbedType.SUCCESS, "LEVEL UP", "You have leveled up to `LEVEL " + i + "` \uD83C\uDF89", 1);
                RankedBot.getGuild().getTextChannelById(Config.getValue("alerts-channel")).sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();

                return;
            }
        }

        if (ClanCache.getClan(this) != null) {
            Clan clan = ClanCache.getClan(this);
            clan.setXp(clan.getXp()+Integer.parseInt(Config.getValue("clanxp-win")));

            for (int i = clan.getLevel().getLevel()+1; i < ClanLevelCache.getClanLevels().size(); i++) {
                if (clan.getXp() >= ClanLevelCache.getLevel(i).getNeededXP()) {
                    clan.setLevel(ClanLevelCache.getLevel(i));

                    Embed embed = new Embed(EmbedType.SUCCESS, "CLAN LEVEL UP", "Clan " + clan.getName() + " has leveled up to `LEVEL " + i + "` \uD83C\uDF89", 1);
                    RankedBot.getGuild().getTextChannelById(Config.getValue("alerts-channel")).sendMessage("<@" + clan.getLeader().getID() + ">").setEmbeds(embed.build()).queue();

                    return;
                }
            }
        }
    }

    public int getPlacement(Statistic statistic) {

        List<String> lb = Leaderboard.getLeaderboard(statistic);

        for (String s : lb) {
            if (s.startsWith(ID)) {
                return lb.indexOf(s)+1;
            }
        }

        return 0;
    }

    public Rank getRank() {
        for (Rank r : RankCache.getRanks().values()) {
            if (elo >= r.getStartingElo() && elo <= r.getEndingElo()) {
                return r;
            }
        }

        return null;
    }

    // boolean - was the action successful
    public boolean ban(LocalDateTime time, String reason) {
        if (isBanned) {
            return false;
        }

        isBanned = true;

        bannedTill = time;
        banReason = reason;

        fix();

        return true;
    }

    public void unban() {
        isBanned = false;
        bannedTill = null;
        banReason = null;
        fix();
    }

    public void joinParty(Party party) {
        party.getMembers().add(this);
        party.getInvitedPlayers().remove(this);
    }

    public double getStatistic(Statistic s) {
        if (s == Statistic.ELO) {
            return elo;
        }
        if (s == Statistic.PEAKELO) {
            return peakElo;
        }

        if (s == Statistic.WINS) {
            return wins;
        }

        if (s == Statistic.LOSSES) {
            return losses;
        }

        if (s == Statistic.WINSTREAK) {
            return winStreak;
        }

        if (s == Statistic.HIGHESTWS) {
            return highestWS;
        }

        if (s == Statistic.HIGHESTLS) {
            return highestLS;
        }

        if (s == Statistic.MVP) {
            return mvp;
        }

        if (s == Statistic.KILLS) {
            return kills;
        }

        if (s == Statistic.DEATHS) {
            return deaths;
        }

        if (s == Statistic.STRIKES) {
            return strikes;
        }

        if (s == Statistic.SCORED) {
            return scored;
        }

        if (s == Statistic.GOLD) {
            return gold;
        }

        if (s == Statistic.LEVEL) {
            return level.getLevel();
        }

        if (s == Statistic.XP) {
            return xp;
        }

        if (s == Statistic.WLR) {
            double templosses = getLosses();
            if (getLosses() == 0)
                templosses = 1.0;

            return getWins() / templosses;
        }

        if (s == Statistic.KDR) {
            double tempdeaths = getDeaths();
            if (getDeaths() == 0)
                tempdeaths = 1.0;

            return getKills() / tempdeaths;
        }

        if (s == Statistic.GAMES) {
            return getWins() + getLosses();
        }

        return 0;
    }

    public void leaveParty(Party party) {
        party.getMembers().remove(this);
    }

    public static boolean isRegistered(String ID) {
        return new File("RankedBot/players/" + ID + ".yml").exists();
    }

    public static void writeFile(String ID, String ign) {
        try {
            if (ign != null) {
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
                Player player = PlayerCache.getPlayer(ID);

                BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/players/" + ID + ".yml"));

                bw.write("name: " + player.getIgn() + "\n");
                bw.write("elo: " + player.getElo() + "\n");
                bw.write("peak-elo: " + player.getPeakElo() + "\n");
                bw.write("wins: " + player.getWins() + "\n");
                bw.write("losses: " + player.getLosses() + "\n");
                bw.write("win-streak: " + player.getWinStreak() + "\n");
                bw.write("loss-streak: " + player.getLossStreak() + "\n");
                bw.write("highest-ws: " + player.getHighestWS() + "\n");
                bw.write("highest-ls: " + player.getHighestLS() + "\n");
                bw.write("mvp: " + player.getMvp() + "\n");
                bw.write("kills: " + player.getKills() + "\n");
                bw.write("deaths: " + player.getDeaths() + "\n");
                bw.write("strikes: " + player.getStrikes() + "\n");
                bw.write("scored: " + player.getScored() + "\n");
                bw.write("gold: " + player.getGold() + "\n");
                bw.write("level: " + player.getLevel().getLevel() + "\n");
                bw.write("xp: " + player.getXp() + "\n");
                bw.write("theme: " + player.getTheme().getName() + "\n");

                StringBuilder themes = new StringBuilder();
                for (Theme t : player.getOwnedThemes()) {
                    themes.append(t.getName());
                    if (player.getOwnedThemes().indexOf(t) != player.getOwnedThemes().size() - 1) {
                        themes.append(",");
                    }
                }

                bw.write("owned-themes: " + themes + "\n");
                bw.write("is-banned: " + player.isBanned + "\n");
                bw.write("banned-till: " + player.getBannedTill() + "\n");
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
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

    public LocalDateTime getBannedTill() {
        return bannedTill;
    }

    public void setBannedTill(LocalDateTime bannedTill) {
        this.bannedTill = bannedTill;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }
}
