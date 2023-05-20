package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class GameInfoCmd extends Command {
    public GameInfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        String t1 = "";
        for (Player p : game.getTeam1()) {
            t1 += "• <@" + p.getID() + ">\n";
        }

        String t2 = "";
        for (Player p : game.getTeam2()) {
            t2 += "• <@" + p.getID() + ">\n";
        }

        String remaining = "";
        for (Player p : game.getRemainingPlayers()) {
            remaining += "• <@" + p.getID() + ">\n";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Game`#" + number + "` Info", "state: `" + game.getState() + "`", 1);
        embed.addField("Team 1", t1, true);
        embed.addField("Team 2", t2, true);
        if (!remaining.equals("")) {
            embed.addField("Remaining", remaining, false);
        }
        embed.addField("Map", game.getMap().getName(), true);
        embed.addField("Casual", String.valueOf(game.isCasual()), true);

        if (game.getState() == GameState.VOIDED) {
            embed.addField("Voided By", "<@" + game.getScoredBy().getId() + ">", true);
        } else if (game.getState() == GameState.SCORED) {
            embed.addField("Scored By", "<@" + game.getScoredBy().getId() + ">", true);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
