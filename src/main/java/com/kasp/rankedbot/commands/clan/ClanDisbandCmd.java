package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
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

public class ClanDisbandCmd extends Command {
    public ClanDisbandCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (ClanCache.getClan(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(player);

        if (clan.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-clan-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        clan.disband();

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("clan-disbanded"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
