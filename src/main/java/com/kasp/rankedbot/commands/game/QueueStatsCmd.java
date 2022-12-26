package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class QueueStatsCmd extends Command {
    public QueueStatsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GamesCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GamesCache.getGame(channel.getId());

        String t1 = "";
        for (Player p : game.getTeam1()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            t1 += "• <@" + p.getID() + "> — `" + p.getWins() + "W/" + p.getLosses() + "L` `(" + wlr + "WLR)`\n";
        }

        String t2 = "";
        for (Player p : game.getTeam2()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            t2 += "• <@" + p.getID() + "> — `" + p.getWins() + "W/" + p.getLosses() + "L` `(" + wlr + "WLR)`\n";
        }

        String remaining = "";
        for (Player p : game.getRemainingPlayers()) {
            double templosses = 1;
            if (p.getLosses() > 0)
                templosses = p.getLosses();

            double wlr = p.getWins() / templosses;
            remaining += "• <@" + p.getID() + "> — `" + p.getWins() + "W/" + p.getLosses() + "L` `(" + wlr + "WLR)`\n";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Game`#" + game.getNumber() + "` QueueStats", "", 1);
        embed.addField("Team 1", t1, true);
        embed.addField("Team 2", t2, true);
        if (!remaining.equals("")) {
            embed.addField("Remaining", remaining, false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
