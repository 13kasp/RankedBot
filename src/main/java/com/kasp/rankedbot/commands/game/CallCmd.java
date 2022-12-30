package com.kasp.rankedbot.commands.game;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CallCmd extends Command {
    public CallCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (GameCache.getGame(channel.getId()) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-game-channel"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        if (sender.getVoiceState() == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "You aren't in a vc", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "", "Given <@" + ID + "> access to your vc", 1);

        sender.getVoiceState().getChannel().getManager().setUserLimit(sender.getVoiceState().getChannel().getUserLimit()+1).queue();
        sender.getVoiceState().getChannel().createPermissionOverride(guild.getMemberById(ID)).setAllow(Permission.VIEW_CHANNEL).setAllow(Permission.VOICE_CONNECT).queue();

        msg.replyEmbeds(embed.build()).queue();
    }
}
