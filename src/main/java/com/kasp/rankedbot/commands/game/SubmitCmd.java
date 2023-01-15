package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Queue;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.cache.QueueCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SubmitCmd extends Command {
    public SubmitCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (msg.getAttachments().size() != Integer.parseInt(Config.getValue("submitting-attachments"))) {
            Embed error = new Embed(EmbedType.ERROR, "", "You need to attach " + Config.getValue("submitting-attachments") + " screenshot(s) for proof", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        Game game = GameCache.getGame(channel.getId());

        if (game.getState() != GameState.PLAYING) {
            Embed error = new Embed(EmbedType.ERROR, "", "You cannot perform this command in the current state of the game", 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (game.isCasual()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("casual-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "Game `#" + game.getNumber() + "` submitted", "", 1);

        String queues = "";
        for (Queue q : QueueCache.getQueues().values()) {
            queues += "<#" + q.getID() + ">\n";
        }
        embed.addField("Requeue here:", queues, false);
        embed.setFooter("RankedBot by kasp#0675");
        channel.sendMessageEmbeds(embed.build()).queue();

        game.setState(GameState.SUBMITTED);

        Embed scoring = new Embed(EmbedType.DEFAULT, "Game `#" + game.getNumber() + "` Scoring", "", 1);

        String t1 = "";
        String t2 = "";
        for (Player p : game.getTeam1())
            t1 += "• <@" + p.getID() + ">\n";
        for (Player p : game.getTeam2())
            t2 += "• <@" + p.getID() + ">\n";

        scoring.addField("Team 1:", t1, true);
        scoring.addField("Team 2:", t2, true);

        if (msg.getAttachments().size() > 0) {
            scoring.setImageURL(msg.getAttachments().get(0).getUrl());
        }

        scoring.setDescription("Map: `" + game.getMap().getName() + "`\n\nPlease use `=score` to score this game");

        channel.sendMessage("<@&" + Config.getValue("scorer-role") + ">").setEmbeds(scoring.build()).queue();
    }
}
