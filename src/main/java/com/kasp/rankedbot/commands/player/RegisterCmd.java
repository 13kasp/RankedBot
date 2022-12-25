package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RegisterCmd extends Command {

    public RegisterCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("wrong-usage").replaceAll("%usage%", "register <ign>"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (Player.isRegistered(sender.getId())) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-registered"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ign = args[1];
        ign = ign.replaceAll(" ", "").trim();

        if (ign.length() > 16) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("ign-too-long"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = new Player(sender.getId(), ign);
        player.fix();

        Embed reply = new Embed(EmbedType.SUCCESS, "Error", Msg.getMsg("successfully-registered"), 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
