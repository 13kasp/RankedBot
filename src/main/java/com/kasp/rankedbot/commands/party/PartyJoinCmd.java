package com.kasp.rankedbot.commands.party;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Party;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PartyCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PartyJoinCmd extends Command {
    public PartyJoinCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PartyCache.getParty(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player leader = PlayerCache.getPlayer(ID);

        if (PartyCache.getParty(leader) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(leader);

        if (!party.getInvitedPlayers().contains(PlayerCache.getPlayer(sender.getId()))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-invited"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (party.getMembers().size() >= Integer.parseInt(Config.getValue("max-party-members"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("this-party-full"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        int partyElo = 0;
        for (Player p : party.getMembers()) {
            partyElo += p.getElo();
        }

        if (partyElo + player.getElo() > Integer.parseInt(Config.getValue("max-party-elo"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "You have too much elo to join this party\nParty elo: `" + partyElo + "`\nYour elo: `" + player.getElo() + "`\nParty elo limit: `" + Config.getValue("max-party-elo") + "`", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.joinParty(party);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("joined-party"), 1);
        msg.replyEmbeds(reply.build()).queue();

        Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + player.getID() + "> has joined your party", 1);
        channel.sendMessage("<@" + leader.getID() + ">").setEmbeds(embed.build()).queue();
    }
}