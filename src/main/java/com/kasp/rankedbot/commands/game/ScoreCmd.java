package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Objects;

public class ScoreCmd extends Command {
    public ScoreCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 4) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (GamesCache.getGame(number) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GamesCache.getGame(number);

        if (game.getState() != GameState.SUBMITTED) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-submitted"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String team = args[2];
        String mvpID = args[3].replaceAll("[^0-9]","");

        List<Player> winningTeam;
        List<Player> losingTeam;

        if (team.equals("1")) {
            winningTeam = game.getTeam1();
            losingTeam = game.getTeam2();
        }
        else {
            winningTeam = game.getTeam2();
            losingTeam = game.getTeam1();
        }

        game.scoreGame(winningTeam, losingTeam, PlayerCache.getPlayer(mvpID), sender);

        Embed embed = new Embed(EmbedType.DEFAULT, "Game `#" + game.getNumber() + "` has been scored", "", 1);

        String team1 = "";
        for (Player p : game.getTeam1()) {
            team1 += "• <@" + p.getID() + "> `(+)`**" + game.getEloGain().get(p) + "** `" + (p.getElo() - game.getEloGain().get(p)) + "` > `" + p.getElo() + "`\n";
        }

        String team2 = "";
        for (Player p : game.getTeam2()) {
            team2 += "• <@" + p.getID() + "> `(+)`**" + game.getEloGain().get(p) + "** `" + (p.getElo() - game.getEloGain().get(p)) + "` > `" + p.getElo() + "`\n";
        }

        embed.addField("Team 1:", team1, false);
        embed.addField("Team 2:", team2, false);

        embed.addField("Scored by", sender.getAsMention(), false);

        if (!Objects.equals(Config.getValue("scored-announcing"), null)) {
            guild.getTextChannelById(Config.getValue("scored-announcing")).sendMessageEmbeds(embed.build()).queue();
        }

        msg.replyEmbeds(embed.build()).queue();

        game.closeChannel(Integer.parseInt(Config.getValue("game-deleting-time")));
    }
}
