package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.ClanLevelCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        writeFile();
    }

    // LOAD CLAN
    public Clan(String name) {
        invitedPlayers = new ArrayList<>();
        this.name = name;

        Yaml yaml = new Yaml();
        try {
            // DATA
            Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/clans/" + name + "/data.yml"));

            this.leader = PlayerCache.getPlayer(data.get("leader").toString());
            this.members = new ArrayList<>();
            for (String ID : data.get("members").toString().split(",")) {
                members.add(PlayerCache.getPlayer(ID));
            }
            this.reputation = Integer.parseInt(data.get("reputation").toString());
            this.xp = Integer.parseInt(data.get("clan-xp").toString());
            this.level = ClanLevelCache.getLevel(Integer.parseInt(data.get("clan-level").toString()));
            this.wins = Integer.parseInt(data.get("wins").toString());
            this.losses = Integer.parseInt(data.get("losses").toString());

            // SETTINGS
            Map<String, Object> settings = yaml.load(new FileInputStream("RankedBot/clans/" + name + "/settings.yml"));

            this.isPrivate = Boolean.parseBoolean(settings.get("private").toString());
            this.eloJoinReq = Integer.parseInt(settings.get("elo-join-req").toString());
            this.description = settings.get("description").toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ClanCache.initializeClan(name, this);
    }

    public void disband() {
        ClanCache.removeClan(name);

        try {
            Files.deleteIfExists(Path.of("RankedBot/clans/" + name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        new File("RankedBot/clans/" + name).mkdirs();

        if (!new File("RankedBot/clans/" + name + "/data.yml").exists()) {
            description = "A newly created PRBW clan";
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("RankedBot/clans/" + name + "/data.yml"));
            bw.write("leader: " + leader.getID() + "\n");
            String members = "";
            for (int i = 0; i < this.members.size(); i++) {
                members += this.members.get(i).getID();
                if (i != this.members.size() - 1) {
                    members += ",";
                }
            }
            bw.write("members: " + members + "\n");
            bw.write("reputation: " + reputation + "\n");
            bw.write("clan-xp: 0\n");
            bw.write("clan-level: 0\n");
            bw.write("wins: 0\n");
            bw.write("losses: 0\n");
            bw.close();

            BufferedWriter bw2 = new BufferedWriter(new FileWriter("RankedBot/clans/" + name + "/settings.yml"));
            bw2.write("private: " + isPrivate + "\n");
            bw2.write("elo-join-req: " + eloJoinReq + "\n");
            bw2.write("# We recommend keeping this below 30 characters\n");
            bw2.write("description: " + description + "\n");
            bw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }

    public void setEloJoinReq(int eloJoinReq) {
        this.eloJoinReq = eloJoinReq;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setLevel(ClanLevel level) {
        this.level = level;
    }
}
