package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.cache.MapsCache;
import com.kasp.rankedbot.instance.embed.Embed;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private int number;

    // discord
    private Guild guild;

    private Category channelsCategory;
    private Category vcsCategory;

    private TextChannel channel;
    private VoiceChannel vc1;
    private VoiceChannel vc2;

    private Queue queue;

    // game
    private List<Player> players;

    private Player captain1;
    private Player captain2;

    private List<Player> team1;
    private List<Player> team2;
    private List<Player> remainingPlayers;

    private GameState state;

    private boolean casual;
    private GameMap map;
    private Player mvp;
    private Member scoredBy;

    public Game(List<Player> players, Queue queue) {
        guild = RankedBot.getGuild();
        this.players = players;
        this.queue = queue;

        RankedBot.serverStats.setGamesPlayed(RankedBot.serverStats.getGamesPlayed()+1);
        this.number = RankedBot.serverStats.getGamesPlayed();

        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();

        this.state = GameState.STARTING;

        this.casual = queue.isCasual();

        List<GameMap> maps = (List<GameMap>) MapsCache.getMaps().values();
        Collections.shuffle(maps);
        this.map = maps.get(0);

        this.channelsCategory = guild.getCategoryById(Config.getValue("game-channels-category"));
        this.vcsCategory = guild.getCategoryById(Config.getValue("game-vcs-category"));

        channel = channelsCategory.createTextChannel(Config.getValue("game-channel-names").replaceAll("%number%", number + "").replaceAll("%mode%", queue.getPlayersEachTeam() + "v" + queue.getPlayersEachTeam())).complete();
        vc1 = vcsCategory.createVoiceChannel(Config.getValue("game-vc-names").replaceAll("%number%", number + "").replaceAll("%mode%", queue.getPlayersEachTeam() + "v" +queue.getPlayersEachTeam()).replaceAll("%team%", "1")).setUserlimit(queue.getPlayersEachTeam()).complete();
        vc2 = vcsCategory.createVoiceChannel(Config.getValue("game-vc-names").replaceAll("%number%", number + "").replaceAll("%mode%", queue.getPlayersEachTeam() + "v" +queue.getPlayersEachTeam()).replaceAll("%team%", "2")).setUserlimit(queue.getPlayersEachTeam()).complete();

        Collections.shuffle(players);
        this.captain1 = players.get(0);
        this.captain2 = players.get(1);

        this.team1.add(captain1);
        this.team2.add(captain2);

        remainingPlayers = players;
        remainingPlayers.remove(captain1);
        remainingPlayers.remove(captain2);

        for (Player p : players) {
            channel.createPermissionOverride(guild.getMemberById(p.getID())).setAllow(Permission.VIEW_CHANNEL).queue();
            vc1.createPermissionOverride(guild.getMemberById(p.getID())).setAllow(Permission.VIEW_CHANNEL).setAllow(Permission.VOICE_CONNECT).queue();
            vc2.createPermissionOverride(guild.getMemberById(p.getID())).setAllow(Permission.VIEW_CHANNEL).setAllow(Permission.VOICE_CONNECT).queue();
        }

        GamesCache.initializeGame(number, this);
    }

    public void pickTeams() {

        if (queue.getPickingMode() == PickingMode.AUTOMATIC) {

            for (int i = 0; i < queue.getPlayersEachTeam() * 2 - 2; i+=2) {
                this.team1.add(remainingPlayers.get(i));
                this.team2.add(remainingPlayers.get(i+1));
            }

            sendGameMsg();

            start();

        }
        else {

            for (Player p : remainingPlayers) {
                guild.moveVoiceMember(guild.getMemberById(p.getID()), vc1).queue();
            }

            sendGameMsg();
        }

    }

    public void start() {
        state = GameState.PLAYING;
    }

    public void sendGameMsg() {
        String mentions = "";
        for (Player p : players) {
            mentions += guild.getMemberById(p.getID()).getAsMention();
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Game `#" + number + "`", "", 1);

        String team1 = "";
        for (Player p : this.team1) {
            team1 += "• <@!" + p.getID() + ">\n";
        }
        embed.addField("Team 1", team1, true);

        String team2 = "";
        for (Player p : this.team2) {
            team2 += "• <@!" + p.getID() + ">\n";
        }
        embed.addField("Team 2", team2, true);

        if (remainingPlayers.size() != 0) {
            String remaining = "";
            for (Player p : remainingPlayers) {
                remaining += "• <@!" + p.getID() + ">\n";
            }

            embed.addField("Remaining", remaining, false);
        }

        embed.addField("Randomly Picked Map", "**" + map + "** — `Height: " + map.getHeight() + "` (" + map.getTeam1() + " vs " + map.getTeam2() + ")", false);

        if (remainingPlayers.size() == 0) {
            embed.setTitle("Game `#" + number + "` has started!");
            if (casual) {
                embed.setDescription("You queued a casual queue meaning this game will have no impact on players' stats");
            }
        }

        channel.sendMessage(mentions).setEmbeds(embed.build()).queue();
    }

    public int getNumber() {
        return number;
    }

    public TextChannel getChannel() {
        return channel;
    }
}
