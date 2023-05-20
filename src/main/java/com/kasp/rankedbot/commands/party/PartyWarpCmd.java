package com.kasp.rankedbot.commands.party;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
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

public class PartyWarpCmd extends Command {
    public PartyWarpCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (party.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-party-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String warped = "";
        for (Player p : party.getMembers()) {
            if (!p.getID().equals(sender.getId())) {
                try {
                    guild.moveVoiceMember(guild.getMemberById(p.getID()), sender.getVoiceState().getChannel()).queue();
                    warped += "<@" + p.getID() + "> ";
                } catch (Exception ignored) {}
            }
        }

        Embed embed;
        if (warped.equals("")) {
            embed = new Embed(EmbedType.ERROR, "", Msg.getMsg("couldnt-warp"), 1);
        }
        else {
            embed = new Embed(EmbedType.SUCCESS, "", "Warped " + warped + " to your vc", 1);
        }
        msg.replyEmbeds(embed.build()).queue();
    }
}
