package com.kasp.rankedbot.commands.utilities;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Queue;
import com.kasp.rankedbot.instance.cache.QueueCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

public class DeleteQueueCmd extends Command {
    public DeleteQueueCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        VoiceChannel vc = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {vc = guild.getVoiceChannelById(ID);}catch (Exception ignored){}

        if (vc == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-vc"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (!QueueCache.containsQueue(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("q-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Queue.delete(ID);

        Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("q-deleted"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
