package com.kasp.rankedbot;

import com.kasp.rankedbot.commands.CommandManager;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.*;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.PagesEvents;
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
import org.yaml.snakeyaml.Yaml;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RankedBot {

    public static JDA jda;
    public static ServerStats serverStats;

    public static String version = "1.0.0";
    public static Guild guild;

    public static void main(String[] args) throws FileNotFoundException {

        new File("RankedBot/players").mkdirs();
        new File("RankedBot/ranks").mkdirs();
        new File("RankedBot/maps").mkdirs();
        new File("RankedBot/queues").mkdirs();
        new File("RankedBot/fonts").mkdirs();
        new File("RankedBot/themes").mkdirs();

        Config.loadConfig();
        Perms.loadPerms();
        Msg.loadMsg();

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(new FileInputStream("RankedBot/config.yml"));
        JDABuilder jdaBuilder = JDABuilder.createDefault(data.get("token").toString());
        jdaBuilder.setStatus(OnlineStatus.valueOf(data.get("status").toString()));
        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL);
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MESSAGES);
        jdaBuilder.addEventListeners(new CommandManager(), new PagesEvents(), new QueueJoin(), new ServerJoin());
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        guild = jda.getGuilds().get(0);

        if (!new File("RankedBot/serverstats.yml").exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("RBW/serverstats.yml"));

                bw.write("games-played: 0");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> serverStatsData = null;
        try {
            serverStatsData = yaml.load(new FileInputStream("RankedBot/serverstats.yml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        serverStats = new ServerStats(Integer.parseInt(serverStatsData.get("games-played").toString()));

        if (new File("RankedBot/ranks").listFiles().length > 0) {
            for (File f : new File("RankedBot/ranks").listFiles()) {
                new Rank(f.getName().replaceAll(".yml", ""));
            }
        }

        if (new File("RankedBot/maps").listFiles().length > 0) {
            for (File f : new File("RankedBot/maps").listFiles()) {
                new GameMap(f.getName().replaceAll(".yml", ""));
            }
        }

        if (new File("RankedBot/queues").listFiles().length > 0) {
            for (File f : new File("RankedBot/queues").listFiles()) {
                new Queue(f.getName().replaceAll(".yml", ""));
            }
        }

        if (new File("RankedBot/themes").listFiles().length > 0) {
            for (File f : new File("RankedBot/themes").listFiles()) {
                new Theme(f.getName().replaceAll(".yml", ""));
            }
        }

        if (new File("RankedBot/players").listFiles().length > 0) {
            for (File f : new File("RankedBot/players").listFiles()) {
                new Player(f.getName().replaceAll(".yml", ""));
            }
        }

        if (new File("RankedBot/games").listFiles().length > 0) {
            for (File f : new File("RankedBot/games").listFiles()) {
                new Game(Integer.parseInt(f.getName().replaceAll(".yml", "")));
            }
        }

        System.out.println("RankedBot has been successfully enabled!");
        System.out.println("NOTE: this bot can only be used on 1 server, otherwise it'll break");
        System.out.println("don't forget to configure config.yml and permissions.yml before using it. You can also edit messages.yml (optional)");
        System.out.println("WARNING: do not restart / stop this bot without executing the command =savedata to prevent data loss");
        System.out.println("Player and game data saves automatically every 2 hours");

        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                System.out.println("[!] Saving players and games");
                for (Player p : PlayerCache.getPlayers().values()) {
                    Player.writeFile(p.getID(), null);
                }
                System.out.println("- Players data successfully saved");
                for (Game g : GamesCache.getGames().values()) {
                        Game.writeFile(g);
                }
                System.out.println("- Games data successfully saved");
            }
        };

        new Timer().schedule (hourlyTask, 0L, 1000*60*60*2);
    }

    public static Guild getGuild() {
        return guild;
    }
}