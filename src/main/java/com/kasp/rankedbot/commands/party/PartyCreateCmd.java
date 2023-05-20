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

public class PartyCreateCmd extends Command {
    public PartyCreateCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
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

        if (PartyCache.getParty(player) != null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-in-party"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        new Party(player);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("party-created"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
