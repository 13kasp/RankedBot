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

public class PartyPromoteCmd extends Command {
    public PartyPromoteCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PartyCache.getParty(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(player);

        if (party.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-party-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player promoted = PlayerCache.getPlayer(ID);

        if (!party.getInvitedPlayers().contains(promoted)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-in-your-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        party.promote(promoted);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "Player <@" + ID + "> has been promoted and is now your party leader", 1);
        msg.replyEmbeds(reply.build()).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, "", "You have been promoted to leader in <@" + sender.getId() + ">'s party", 1);
        channel.sendMessage("<@" + ID + ">").setEmbeds(embed.build()).queue();
    }
}
