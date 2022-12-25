package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

import java.util.*;

public class ScreenshareCmd extends Command {
    public ScreenshareCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("wrong-usage").replaceAll("%usage%", "ss <ID/mention> <reason>"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (msg.getAttachments().size() != Integer.parseInt(Config.getValue("ss-attachments"))) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", "You need to attach " + Config.getValue("ss-attachments") + " images for proof", 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]","");
        String reason = msg.getContentRaw().replaceAll(args[0], "").replaceAll(args[1], "").trim();

        if (sender == guild.retrieveMemberById(ID).complete()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("ss-self"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        List<Role> freeze = new ArrayList<>();
        freeze.add(guild.getRoleById(Config.getValue("frozen-role")));
        guild.modifyMemberRoles(guild.retrieveMemberById(ID).complete(), freeze, null).queue();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                guild.modifyMemberRoles(guild.retrieveMemberById(ID).complete(), null, freeze).queue();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, Integer.parseInt(Config.getValue("time-till-unfrozen")) * 60000L);

        Embed embed = new Embed(EmbedType.DEFAULT, "DON'T LOG OFF", "", 1);
        embed.setDescription(guild.retrieveMemberById(ID).complete().getAsMention() + " you have been SS requested\nDO NOT log off or modify/delete any files on your pc\n" +
                "if no screensharer turns up, you're free to go after " + Config.getValue("time-till-unfrozen") + "mins\n\n" +
                "**SS reason**: " + reason + "\n\n**Requested by**: " + sender.getAsMention());

        guild.getTextChannelById(Config.getValue("ssreq-channel")).sendMessage(guild.retrieveMemberById(ID).complete().getAsMention()).setEmbeds(embed.build()).queue();

        if (!Objects.equals(Config.getValue("ss-roles"), null)) {
            String roles = "";
            for (String s : Config.getValue("ss-roles").split(",")) {
                roles+=guild.getRoleById(s).getAsMention();
            }
            guild.getTextChannelById(Config.getValue("ssreq-channel")).sendMessage(roles + " Please screenshare this player").queue();
        }

        msg.reply("screenshare request sent in " + guild.getTextChannelById(Config.getValue("ssreq-channel")).getAsMention()).queue();
    }
}
