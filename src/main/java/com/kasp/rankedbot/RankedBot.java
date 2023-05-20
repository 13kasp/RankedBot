package com.kasp.rankedbot;

import com.kasp.rankedbot.commands.CommandManager;
import com.kasp.rankedbot.commands.moderation.UnbanTask;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.database.*;
import com.kasp.rankedbot.instance.*;
import com.kasp.rankedbot.levelsfile.Levels;
import com.kasp.rankedbot.listener.PagesEvents;
import com.kasp.rankedbot.listener.PartyInviteButton;
import com.kasp.rankedbot.listener.QueueJoin;
import com.kasp.rankedbot.listener.ServerJoin;
import com.kasp.rankedbot.messages.Msg;
import com.kasp.rankedbot.perms.Perms;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class RankedBot {

    public static JDA jda;

    public static String version = "1.1.2";
    public static Guild guild;

    public static void main(String[] args) {

        new File("RankedBot/fonts").mkdirs();
        new File("RankedBot/themes").mkdirs();

        SQLite.connect();
        SQLTableManager.createPlayersTable();
        SQLTableManager.createRanksTable();
        SQLTableManager.createGamesTable();
        SQLTableManager.createMapsTable();
        SQLTableManager.createQueuesTable();
        SQLTableManager.createClansTable();

        Config.loadConfig();
        Perms.loadPerms();
        Msg.loadMsg();
        Levels.loadLevels();
        Levels.loadClanLevels();

        if (Config.getValue("token") == null) {
            System.out.println("[!] Please set your token in config.yml");
            return;
        }

        JDABuilder jdaBuilder = JDABuilder.createDefault(Config.getValue("token"));
        jdaBuilder.setStatus(OnlineStatus.valueOf(Config.getValue("status").toUpperCase()));
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        jdaBuilder.addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin(), new PartyInviteButton());
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        System.out.println("\n[!] Finishing up... this might take around 10 seconds\n");

        // get guild
        TimerTask task = new TimerTask () {
            @Override
            public void run () {
                if (jda.getGuilds().size() == 0) {
                    System.out.println("[!] Please invite this bot to your server first");
                    return;
                }

                guild = jda.getGuilds().get(0);

                for (int i = 1; i <= SQLUtilsManager.getRankSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT discordID FROM ranks where _ID='" + i +"'");
                    try {
                        new Rank(resultSet.getString(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a rank could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                for (int i = 1; i <= SQLUtilsManager.getMapSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT name FROM maps where _ID='" + i +"'");
                    try {
                        new GameMap(resultSet.getString(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a map could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                for (int i = 1; i <= SQLUtilsManager.getQueueSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT discordID FROM queues where _ID='" + i +"'");
                    try {
                        new Queue(resultSet.getString(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a queue could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                if (new File("RankedBot/themes").listFiles().length > 0) {
                    for (File f : new File("RankedBot/themes").listFiles()) {
                        new Theme(f.getName().replaceAll(".png", ""));
                    }
                }

                for (int i = 0; i <= Integer.parseInt(Levels.levelsData.get("total-levels")); i++) {
                    new Level(i);
                }

                for (int i = 0; i <= Integer.parseInt(Levels.clanLevelsData.get("total-levels")); i++) {
                    new ClanLevel(i);
                }

                for (int i = 1; i <= SQLPlayerManager.getPlayerSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT discordID FROM players where _ID='" + i +"'");
                    try {
                        new Player(resultSet.getString(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a player could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                for (int i = 1; i <= SQLGameManager.getGameSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT number FROM games where _ID='" + i +"'");
                    try {
                        new Game(resultSet.getInt(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a game could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                for (int i = 1; i <= SQLClanManager.getClanSize(); i++) {
                    ResultSet resultSet = SQLite.queryData("SELECT name FROM clans where _ID='" + i +"'");
                    try {
                        new Clan(resultSet.getString(1));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("[!] a clan could not be loaded! Please make a bug report on support discord asap");
                    }
                }

                System.out.println("-------------------------------");

                System.out.println("RankedBot has been successfully enabled!");
                System.out.println("NOTE: this bot can only be in 1 server, otherwise it'll break");
                System.out.println("don't forget to configure config.yml and permissions.yml before using it\nYou can also edit messages.yml (optional)");

                System.out.println("-------------------------------");
            }
        };

        new Timer().schedule(task, 10000);

        TimerTask unbanTask = new TimerTask () {
            @Override
            public void run () {
                UnbanTask.checkAndUnbanPlayers();
            }
        };

        new Timer().schedule(unbanTask, 1000 * 60 * 60, 1000 * 60 * 60);
    }

    public static Guild getGuild() {
        return guild;
    }
}