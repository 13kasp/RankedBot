package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Timer;
import java.util.TimerTask;

public class VoidCmd extends Command {
    public VoidCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        Game game = GameCache.getGame(channel.getId());

        int number = game.getNumber();

        Embed embed = new Embed(EmbedType.DEFAULT, "Game`#" + number + "` Voiding", "votes will be counted in `30s`", 1);
        embed.addField("Requested by: ", sender.getAsMention(), false);
        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            message.addReaction("✔").queue();
            message.addReaction("❌").queue();


            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Message message1 = channel.retrieveMessageById(message.getId()).complete();

                    if (message1.getReactions().get(0).getCount() - 1 < message1.getReactions().get(1).getCount()) {
                        Embed reply = new Embed(EmbedType.ERROR, "", "Voiding has been cancelled", 1);
                        msg.replyEmbeds(reply.build()).queue();
                        return;
                    }

                    Embed done = new Embed(EmbedType.DEFAULT, "Game`#" + number + "` Has Been Voided", "if this command was abused, please screenshot this and make a report ticket\n\ngame channel closing in `60s`", 1);

                    done.addField("Requested by: ", sender.getAsMention(), false);
                    game.setState(GameState.VOIDED);
                    game.setScoredBy(sender);

                    game.closeChannel(60);

                    message1.editMessageEmbeds(done.build()).queue();
                    message1.clearReactions().queue();
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 30000);
        });
    }
}
