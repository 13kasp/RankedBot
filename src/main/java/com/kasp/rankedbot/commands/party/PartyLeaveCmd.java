package com.kasp.rankedbot.commands.party;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Party;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PartyCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PartyLeaveCmd extends Command {
    public PartyLeaveCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PartyCache.getParty(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(player);

        if (party.getLeader() == player) {
            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("party-disbanded"), 1);
            msg.replyEmbeds(reply.build()).queue();

            String mentions = "";
            for (Player p : party.getMembers()) {
                mentions += "<@" + p.getID() + ">";
            }

            Embed embed = new Embed(EmbedType.DEFAULT, "", Msg.getMsg("your-party-disbanded"), 1);
            channel.sendMessage(mentions).setEmbeds(embed.build()).queue();

            party.disband();
        }
        else {
            player.leaveParty(party);

            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("party-left"), 1);
            msg.replyEmbeds(reply.build()).queue();

            Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + player.getID() + "> has left your party", 1);
            channel.sendMessage("<@" + party.getLeader().getID() + ">").setEmbeds(embed.build()).queue();
        }
    }
}
