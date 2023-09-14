package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.database.SQLPlayerManager;
import com.kasp.rankedbot.database.SQLite;
import com.kasp.rankedbot.instance.cache.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("ID: " + ID);
        this.ID = ID;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM players WHERE discordID='" + ID + "';");

        try {
            this.ign = resultSet.getString(2);
            this.elo = resultSet.getInt(3);
            this.peakElo = resultSet.getInt(4);
            this.wins = resultSet.getInt(5);
            this.losses = resultSet.getInt(6);
            this.winStreak = resultSet.getInt(7);
            this.lossStreak = resultSet.getInt(8);
            this.highestWS = resultSet.getInt(9);
            this.highestLS = resultSet.getInt(10);
            this.mvp = resultSet.getInt(11);
            this.kills = resultSet.getInt(12);
            this.deaths = resultSet.getInt(13);
            this.strikes = resultSet.getInt(14);
            this.scored = resultSet.getInt(15);
            this.gold = resultSet.getInt(16);
            this.level = LevelCache.getLevel(resultSet.getInt(17));
            this.xp = resultSet.getInt(18);
            this.theme = ThemeCache.getTheme(resultSet.getString(19));

            ownedThemes = new ArrayList<>();

            String[] themes = resultSet.getString(20).split(",");
            for (String s : themes) {
                ownedThemes.add(ThemeCache.getTheme(s));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.isBanned = Boolean.parseBoolean(resultSet.getString(21));
            if (isBanned) {
                this.bannedTill = LocalDateTime.parse(resultSet.getString(22), formatter);
                this.banReason = resultSet.getString(23);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PlayerCache.initializePlayer(this);
    }

    public void fix() {
        Guild guild = RankedBot.getGuild();

        Member member = null;
        if (guild.getMemberById(ID) != null) {
            member = guild.getMemberById(ID);
        }

        ArrayList<Role> rolestoremove = new ArrayList<>();
        ArrayList<Role> rolestoadd = new ArrayList<>();

        rolestoadd.add(guild.getRoleById(Config.getValue("registered-role")));

        if (getRank() != null) {
            Rank rank = getRank();
            rolestoadd.add(guild.getRoleById(rank.getID()));

            for (Rank r : RankCache.getRanks().values()) {
                if (rank != r) {
                    rolestoremove.add(guild.getRoleById(r.getID()));
                }
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

        if (member != null) {
            if (RankedBot.guild.getSelfMember().canInteract(member)) {
                guild.modifyMemberRoles(member, rolestoadd, rolestoremove).queue();
                member.modifyNickname(Config.getValue("elo-formatting").replaceAll("%elo%", elo + "") + ign).queue();
            }
            else {
                System.out.println("[RBW] Couldn't modify " + member.getUser().getAsTag() + "'s roles and nickname");
            }
        }
    }

    public void wipe() {
        setElo(Integer.parseInt(Config.getValue("starting-elo")));
        setPeakElo(Integer.parseInt(Config.getValue("starting-elo")));
        setWins(0);
        setLosses(0);
        setWinStreak(0);
        setLossStreak(0);
        setHighestWS(0);
        setHighestLS(0);
        setMvp(0);
        setKills(0);
        setDeaths(0);
    }

    public void win(double eloMultiplier) {
        if (Boolean.parseBoolean(Config.getValue("levels-enabled"))) {
            updateXP(Integer.parseInt(Config.getValue("play-xp")), Integer.parseInt(Config.getValue("clanxp-play")));
            updateXP(Integer.parseInt(Config.getValue("win-xp")), Integer.parseInt(Config.getValue("clanxp-win")));
        }

        setWins(wins+1);
        setWinStreak(winStreak+1);
        setElo(elo += getRank().getWinElo() * eloMultiplier);

        if (peakElo < elo) {
            setPeakElo(elo);
        }

        if (lossStreak > 0) {
            setLossStreak(0);
        }

        if (highestWS < winStreak) {
            setHighestWS(winStreak);
        }
    }

    public void lose(double eloMultiplier) {
        if (Boolean.parseBoolean(Config.getValue("levels-enabled"))) {
            updateXP(Integer.parseInt(Config.getValue("play-xp")), Integer.parseInt(Config.getValue("clanxp-play")));
        }

        setLosses(losses+1);
        setLossStreak(lossStreak+1);

        if (elo - getRank().getLoseElo() * eloMultiplier > 0) {
            setElo(elo -= getRank().getLoseElo() * eloMultiplier);
        }
        else {
            setElo(0);
        }

        if (winStreak > 0) {
            setWinStreak(0);
        }

        if (highestLS < lossStreak) {
            setHighestLS(lossStreak);
        }
    }

    public void updateXP(int addXp, int addClanXP) {
        setXp(xp+=addXp);

        for (int i = level.getLevel()+1; i < LevelCache.getLevels().size(); i++) {
            if (xp >= LevelCache.getLevel(i).getNeededXP()) {
                setLevel(LevelCache.getLevel(i));

                for (String s : LevelCache.getLevel(i).getRewards()) {
                    if (s.startsWith("GOLD")) {
                        setGold(gold+=Integer.parseInt(s.split("=")[1]));
                    }
                }

                Embed embed = new Embed(EmbedType.SUCCESS, "LEVEL UP", "You have leveled up to `LEVEL " + i + "` \uD83C\uDF89", 1);
                RankedBot.getGuild().getTextChannelById(Config.getValue("alerts-channel")).sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();

                return;
            }
        }

        if (ClanCache.getClan(this) != null) {
            Clan clan = ClanCache.getClan(this);
            clan.setXp(clan.getXp()+addClanXP);

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

        setBanned(true);

        setBannedTill(time);
        setBanReason(reason);

        fix();

        return true;
    }

    public void unban() {
        setBanned(false);
        setBannedTill(null);
        setBanReason(null);
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
        return SQLPlayerManager.isRegistered(ID);
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
        SQLPlayerManager.updateIgn(ID);
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;

        if (peakElo < elo) {
            setPeakElo(elo);
        }

        SQLPlayerManager.updateElo(ID);
    }

    public int getPeakElo() {
        return peakElo;
    }

    public void setPeakElo(int peakElo) {
        this.peakElo = peakElo;
        SQLPlayerManager.updatePeakElo(ID);
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
        SQLPlayerManager.updateWins(ID);
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
        SQLPlayerManager.updateLosses(ID);
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
        SQLPlayerManager.updateWinStreak(ID);
    }

    public int getLossStreak() {
        return lossStreak;
    }

    public void setLossStreak(int lossStreak) {
        this.lossStreak = lossStreak;
        SQLPlayerManager.updateLossStreak(ID);
    }

    public int getHighestWS() {
        return highestWS;
    }

    public void setHighestWS(int highestWS) {
        this.highestWS = highestWS;
        SQLPlayerManager.updateHighestWS(ID);
    }

    public int getHighestLS() {
        return highestLS;
    }

    public void setHighestLS(int highestLS) {
        this.highestLS = highestLS;
        SQLPlayerManager.updateHighestLS(ID);
    }

    public int getMvp() {
        return mvp;
    }

    public void setMvp(int mvp) {
        this.mvp = mvp;
        SQLPlayerManager.updateMvp(ID);
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
        SQLPlayerManager.updateStrikes(ID);
    }

    public int getScored() {
        return scored;
    }

    public void setScored(int scored) {
        this.scored = scored;
        SQLPlayerManager.updateScored(ID);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        SQLPlayerManager.updateKills(ID);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        SQLPlayerManager.updateDeaths(ID);
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
        SQLPlayerManager.updateGold(ID);
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
        SQLPlayerManager.updateLevel(ID);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        SQLPlayerManager.updateXP(ID);
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        SQLPlayerManager.updateTheme(ID);
    }

    public ArrayList<Theme> getOwnedThemes() {
        return ownedThemes;
    }

    public void giveTheme(Theme theme) {
        ownedThemes.add(theme);
        SQLPlayerManager.updateOwnedThemes(ID);
    }

    public void removeTheme(Theme theme) {
        ownedThemes.remove(theme);
        SQLPlayerManager.updateOwnedThemes(ID);
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
        SQLPlayerManager.updateIsBanned(ID);
    }

    public LocalDateTime getBannedTill() {
        return bannedTill;
    }

    public void setBannedTill(LocalDateTime bannedTill) {
        this.bannedTill = bannedTill;
        SQLPlayerManager.updateBannedTill(ID);
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
        SQLPlayerManager.updateBanReason(ID);
    }
}
