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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankedBot {

    private static JDA jda;
    private static Guild guild;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // Melhora na eficiência de timers
    public static final String version = "1.1.7";

    public static void main(String[] args) {
        createDirectories();
        initializeDatabase();
        loadConfigs();

        if (Config.getValue("token") == null) {
            System.out.println("[!] Please set your token in config.yml");
            return;
        }

        try {
            buildJDA();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        System.out.println("\n[!] Finishing up... this might take around 10 seconds\n");

        // Agendar as tarefas
        scheduler.schedule(RankedBot::loadGuildData, 10, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(UnbanTask::checkAndUnbanPlayers, 1, 1, TimeUnit.HOURS);
    }

    private static void createDirectories() {
        new File("RankedBot/fonts").mkdirs();
        new File("RankedBot/themes").mkdirs();
    }

    private static void initializeDatabase() {
        SQLite.connect();
        SQLTableManager.createPlayersTable();
        SQLTableManager.createRanksTable();
        SQLTableManager.createGamesTable();
        SQLTableManager.createMapsTable();
        SQLTableManager.createQueuesTable();
        SQLTableManager.createClansTable();
    }

    private static void loadConfigs() {
        Config.loadConfig();
        Perms.loadPerms();
        Msg.loadMsg();
        Levels.loadLevels();
        Levels.loadClanLevels();
    }

    private static void buildJDA() throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(Config.getValue("token"));
        jdaBuilder.setStatus(OnlineStatus.valueOf(Config.getValue("status").toUpperCase()));
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES);
        jdaBuilder.addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin(), new PartyInviteButton());

        jda = jdaBuilder.build();
    }

    private static void loadGuildData() {
        if (jda.getGuilds().isEmpty()) {
            System.out.println("[!] Please invite this bot to your server first");
            return;
        }

        guild = jda.getGuilds().get(0);
        loadThemes();
        loadLevels();
        loadDataFromDatabase();
        
        System.out.println("-------------------------------");
        System.out.println("RankedBot has been successfully enabled!");
        System.out.println("NOTE: this bot can only be in 1 server, otherwise it'll break");
        System.out.println("Don't forget to configure config.yml and permissions.yml before using it.");
        System.out.println("-------------------------------");
    }

    private static void loadThemes() {
        File[] themeFiles = new File("RankedBot/themes").listFiles();
        if (themeFiles != null) {
            for (File file : themeFiles) {
                new Theme(file.getName().replaceAll(".png", ""));
            }
        }
    }

    private static void loadLevels() {
        int totalLevels = Integer.parseInt(Levels.levelsData.get("total-levels"));
        for (int i = 0; i <= totalLevels; i++) {
            new Level(i);
        }

        int totalClanLevels = Integer.parseInt(Levels.clanLevelsData.get("total-levels"));
        for (int i = 0; i <= totalClanLevels; i++) {
            new ClanLevel(i);
        }
    }

    private static void loadDataFromDatabase() {
        loadRanks();
        loadMaps();
        loadQueues();
        loadPlayers();
        loadGames();
        loadClans();
    }

    private static void loadRanks() {
        List<String> ranks = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM ranks")) {
            while (rs.next()) {
                ranks.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String rank : ranks) {
            try {
                new Rank(rank);
            } catch (Exception e) {
                System.out.println("[!] a rank could not be loaded! Please report this issue.");
            }
        }
    }

    private static void loadMaps() {
        List<String> maps = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM maps")) {
            while (rs.next()) {
                maps.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String map : maps) {
            try {
                new GameMap(map);
            } catch (Exception e) {
                System.out.println("[!] a map could not be loaded! Please report this issue.");
            }
        }
    }

    private static void loadQueues() {
        List<String> queues = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM queues")) {
            while (rs.next()) {
                queues.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String queue : queues) {
            try {
                new Queue(queue);
            } catch (Exception e) {
                System.out.println("[!] a queue could not be loaded! Please report this issue.");
            }
        }
    }

    private static void loadPlayers() {
        List<String> players = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM players")) {
            while (rs.next()) {
                players.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String player : players) {
            try {
                new Player(player);
            } catch (Exception e) {
                System.out.println("[!] a player could not be loaded! Please report this issue.");
            }
        }
    }

    private static void loadGames() {
        List<String> games = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM games")) {
            while (rs.next()) {
                games.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String game : games) {
            try {
                new Game(Integer.parseInt(game));
            } catch (Exception e) {
                System.out.println("[!] a game could not be loaded! Please report this issue.");
            }
        }
    }

    private static void loadClans() {
        List<String> clans = new ArrayList<>();
        try (ResultSet rs = SQLite.queryData("SELECT * FROM clans")) {
            while (rs.next()) {
                clans.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String clan : clans) {
            try {
                new Clan(clan);
            } catch (Exception e) {
                System.out.println("[!] a clan could not be loaded! Please report this issue.");
            }
        }
    }

    public static Guild getGuild() {
        return guild;
    }

    // Método para encerrar o bot e liberar recursos adequadamente
    public static void shutdown() {
        if (jda != null) {
            jda.shutdown();
        }
        scheduler.shutdown();
        SQLite.disconnect();  // Encerra a conexão com o banco de dados corretamente
    }
}
