package com.kasp.rankedbot.commands.utilities;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Rank;
import com.kasp.rankedbot.instance.cache.RankCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

public class DeleteRankCmd extends Command {
    public DeleteRankCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Role role = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {role = guild.getRoleById(ID);}catch (Exception ignored){}

        if (role == null) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("invalid-role"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (!RankCache.containsRank(ID)) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("rank-doesnt-exist"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        Rank.delete(ID);

        Embed success = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("rank-deleted"), 1);
        msg.replyEmbeds(success.build()).queue();
    }
}
