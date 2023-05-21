package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class UndoGameCmd extends Command {
    public UndoGameCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int number = Integer.parseInt(args[1]);

        if (GameCache.getGame(number) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-game"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Game game = GameCache.getGame(number);

        if (game.getState() != GameState.SCORED) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-scored"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        game.undo();

        Embed embed = new Embed(EmbedType.SUCCESS, "Game `#" + game.getNumber() + "` has been undone", "You can re-score it by simply using `=score` again", 1);
        msg.replyEmbeds(embed.build()).queue();

        embed.setType(EmbedType.ERROR);

        embed.addField("Unscored By", sender.getAsMention(), false);
        embed.setDescription("");

        if (!Objects.equals(Config.getValue("scored-announcing"), null)) {
            guild.getTextChannelById(Config.getValue("scored-announcing")).sendMessageEmbeds(embed.build()).queue();
        }
    }
}
