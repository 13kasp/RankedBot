package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.database.SQLClanManager;
import com.kasp.rankedbot.database.SQLite;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.ClanLevelCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Clan {

    // DATA
    private String name;
    private Player leader;
    private List<Player> members;
    private int reputation;
    private int xp;
    private ClanLevel level;

    // CLAN WAR
    private int wins;
    private int losses;

    // SETTINGS
    private boolean isPrivate;
    private int eloJoinReq;
    private String description;

    private List<Player> invitedPlayers;

    // CREATE CLAN
    public Clan(String name, Player leader) {
        members = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        members.add(leader);

        this.name = name;
        this.leader = leader;
        reputation = Integer.parseInt(Config.getValue("clan-starting-rep"));
        level = ClanLevelCache.getLevel(0);

        this.isPrivate = true;
        this.description = "A newly created clan";
        ClanCache.initializeClan(name, this);
        create();
    }

    // LOAD CLAN
    public Clan(String name) {
        invitedPlayers = new ArrayList<>();
        this.name = name;

        ResultSet resultSet = SQLite.queryData("SELECT * FROM clans WHERE name='" + name + "';");

        try {
            this.leader = PlayerCache.getPlayer(resultSet.getString(3));
            this.members = new ArrayList<>();
            for (String ID : resultSet.getString(4).split(",")) {
                members.add(PlayerCache.getPlayer(ID));
            }
            this.reputation = resultSet.getInt(5);
            this.xp = resultSet.getInt(6);
            this.level = ClanLevelCache.getLevel(resultSet.getInt(7));
            this.wins = resultSet.getInt(8);
            this.losses = resultSet.getInt(9);
            this.isPrivate = Boolean.parseBoolean(resultSet.getString(10));
            this.eloJoinReq = resultSet.getInt(11);
            this.description = resultSet.getString(12);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ClanCache.initializeClan(name, this);
    }

    public void disband() {
        ClanCache.removeClan(name);

        try {
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name + "/theme.png"));
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name + "/icon.png"));
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void create() {

        new File("RankedBot/clans/" + name).mkdirs();

        if (!new File("RankedBot/clans/" + name + "/data.yml").exists()) {
            description = "A newly created PRBW clan";
        }

        SQLClanManager.createClan(name, leader.getID());
    }

    public static void deleteIcon(String name) {
        try {
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name + "/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTheme(String name) {
        try {
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name + "/theme.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getReputation() {
        return reputation;
    }

    public int getXp() {
        return xp;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public int getEloJoinReq() {
        return eloJoinReq;
    }

    public String getDescription() {
        return description;
    }

    public List<Player> getInvitedPlayers() {
        return invitedPlayers;
    }

    public String getName() {
        return name;
    }

    public Player getLeader() {
        return leader;
    }

    public List<Player> getMembers() {
        return members;
    }

    public ClanLevel getLevel() {
        return level;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
        SQLClanManager.updatePrivate(name);
    }

    public void setEloJoinReq(int eloJoinReq) {
        this.eloJoinReq = eloJoinReq;
        SQLClanManager.updateEloJoinReq(name);
    }

    public void setDescription(String description) {
        this.description = description;
        SQLClanManager.updateDescription(name);
    }

    public void setXp(int xp) {
        this.xp = xp;
        SQLClanManager.updateXP(name);
    }

    public void setLevel(ClanLevel level) {
        this.level = level;
        SQLClanManager.updateLevel(name);
    }
}
