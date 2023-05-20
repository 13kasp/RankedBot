package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClanCreateCmd extends Command {
    public ClanCreateCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());
        String name = args[1];

        if (name.length() > Integer.parseInt(Config.getValue("clan-name-max"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("name-too-long"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (ClanCache.containsClan(name)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-already-exists"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (ClanCache.getClan(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (player.getElo() < Integer.parseInt(Config.getValue("elo-to-create"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-enough-elo"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (player.getGold() < Integer.parseInt(Config.getValue("gold-to-create"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-enough-gold"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.setGold(player.getGold()-Integer.parseInt(Config.getValue("elo-to-create")));
        new Clan(name, player);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("clan-created"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
