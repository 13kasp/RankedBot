package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClanJoinCmd extends Command {
    public ClanJoinCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (ClanCache.getClan(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (!ClanCache.containsClan(args[1])) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(args[1]);

        if (!clan.getInvitedPlayers().contains(player)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-not-invited"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (clan.getMembers().size() >= Integer.parseInt(Config.getValue("l" + clan.getLevel().getLevel()))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("clan-max-players"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        clan.getMembers().add(player);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("clan-joined"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
