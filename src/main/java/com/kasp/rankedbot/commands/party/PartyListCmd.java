package com.kasp.rankedbot.commands.party;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
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

public class PartyListCmd extends Command {
    public PartyListCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID;

        if (args.length == 2) {
            ID = args[1].replaceAll("[^0-9]", "");
        }
        else {
            ID = sender.getId();
        }

        if (PartyCache.getParty(PlayerCache.getPlayer(ID)) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Party party = PartyCache.getParty(PlayerCache.getPlayer(ID));

        String title = "players `[" + party.getMembers().size() + "/" + Config.getValue("max-party-members") + "]`";
        String players = "";

        for (Player p : party.getMembers()) {
            players += "<@" + p.getID() + "> ";
        }

        String invited = "";

        for (Player p : party.getInvitedPlayers()) {
            invited += "<@" + p.getID() + "> ";
        }

        Embed embed = new Embed(EmbedType.DEFAULT, party.getLeader().getIgn() + " party info", "", 1);
        embed.addField(title, players, false);

        if (!invited.equals("")) {
            embed.addField("Invited", invited, false);
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
