package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PickCmd extends Command {
    public PickCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
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

        String ID = args[1].replaceAll("[^0-9]","");

        if (!game.pickPlayer(PlayerCache.getPlayer(sender.getId()), PlayerCache.getPlayer(ID))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "Reason: `the game is already started` / `you aren't captain or it's not your turn to pick` / " +
                    "`you're trying to pick yourself` / `you're trying to pick someone not from this game or already picked`", 1);
            msg.replyEmbeds(reply.build()).queue();
        }
    }
}