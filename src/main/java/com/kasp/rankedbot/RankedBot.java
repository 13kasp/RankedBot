package com.kasp.rankedbot;

import com.kasp.rankedbot.classes.embed.PagesEvents;
import com.kasp.rankedbot.commands.CommandManager;
import com.kasp.rankedbot.config.Config;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class Main {

    public static JDA jda;

    public static String version = "3.0.0";
    public static Guild guild;

    public static void main(String[] args) throws FileNotFoundException {

        new File("RankedBot/players").mkdirs();
        new File("RankedBot/ranks").mkdirs();
        new File("RankedBot/maps").mkdirs();
        new File("RankedBot/bans").mkdirs();
        new File("RankedBot/queues").mkdirs();
        new File("RankedBot/games").mkdirs();
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
        jdaBuilder.addEventListeners(new CommandManager(), new PagesEvents());
        try {
            jda = jdaBuilder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        guild = jda.getGuilds().get(0);

        System.out.println("RankedBot has been successfully enabled!");
        System.out.println("NOTE: this bot can only be used on 1 server, otherwise it'll break");
        System.out.println("don't forget to configure config.yml and permissions.yml before using it. You can also edit messages.yml (optional)");
    }

    public static Guild getGuild() {
        return guild;
    }
}