package com.kasp.rbw3.commands.playerCmds;

import com.kasp.rbw3.classes.embed.Embed;
import com.kasp.rbw3.commands.Command;
import com.kasp.rbw3.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Register extends Command {

    public Register(String command, String usage, String[] aliases, String description, String subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed("error", "Error", Msg.getMsg("wrong-usage").replaceAll("%usage%", "register <ign>"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ign = args[1];
        ign = ign.replaceAll(" ", "").trim();

        if (args.length > 16) {

        }
    }
}
