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

public class ClanInfoCmd extends Command {
    public ClanInfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String clanName;
        if (args.length == 1)
            if (ClanCache.getClan(PlayerCache.getPlayer(sender.getId())) != null) {
                clanName = ClanCache.getClan(PlayerCache.getPlayer(sender.getId())).getName();
            }
            else {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }
        else {
            clanName = args[1];
        }

        if (ClanCache.getClan(clanName) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Clan", Msg.getMsg("clan-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(clanName);

        String members = "";
        for (Player p : clan.getMembers()) {
            members += "<@" + p.getID() + "> ";
        }

        String invited = "";
        for (Player p : clan.getInvitedPlayers()) {
            invited += "<@" + p.getID() + "> ";
        }

        String eloReq = "";
        if (!clan.isPrivate()) {
            eloReq = " - `" + clan.getEloJoinReq() + "` ELO required to join";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, clan.getName() + " Clan Info", "- Stats that don't show up on `=cstats`", 1);
        embed.addField("Private", clan.isPrivate() + eloReq, false);
        embed.addField("Clan Leader", "<@" + clan.getLeader().getID() + ">", false);
        embed.addField("All Members `[" + clan.getMembers().size() + "/" + Config.getValue("l" + clan.getLevel().getLevel()) + "]`", members, false);
        if (clan.getInvitedPlayers().size() != 0) {
            embed.addField("All Invited Players `[" + clan.getInvitedPlayers().size() + "]`", invited, false);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
