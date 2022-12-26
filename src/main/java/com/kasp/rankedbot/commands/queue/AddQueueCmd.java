package com.kasp.rankedbot.commands.queue;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.PickingMode;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Queue;
import com.kasp.rankedbot.instance.cache.QueuesCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

public class AddQueueCmd extends Command {
    public AddQueueCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 5) {
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

        int playersEachTeam = Integer.parseInt(args[2]);

        if (playersEachTeam <= 0) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("q-more-players"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (QueuesCache.containsQueue(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("q-already-exists"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        PickingMode pickingMode = PickingMode.valueOf(args[3].toUpperCase());

        boolean casual = Boolean.parseBoolean(args[4]);

        Queue.createFile(ID, playersEachTeam, pickingMode, casual);
        new Queue(ID);
        vc.getManager().setUserLimit(playersEachTeam * 2).queue();

        Embed embed = new Embed(EmbedType.SUCCESS, "✅ successfully added `" + vc.getName() + "` queue", "", 1);
        embed.addField("VC", vc.getAsMention(), true);
        embed.addField("Players in each team:", args[2], true);
        embed.addField("Sorting mode:", args[3], true);
        embed.addField("Casual queue:", casual + "", true);
        embed.addField("IMPORTANT", "don't change the user limit on the queue vc\notherwise the bot might break", false);
        msg.replyEmbeds(embed.build()).queue();
    }
}
