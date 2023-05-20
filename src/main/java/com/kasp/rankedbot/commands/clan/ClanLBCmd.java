package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Leaderboard;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ClanLBCmd extends Command {
    public ClanLBCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        List<Clan> lb = new ArrayList<>(Leaderboard.getClansLeaderboard());

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

        for (int j = 0; j < Math.ceil(lb.size() / 10.0); j++) {
            Embed reply = new Embed(EmbedType.DEFAULT, "Clans Leaderboard", "this lb is for `reputation`\nwhich can be obtained\nby playing clan wars", (int) Math.ceil(lb.size() / 10.0));

            String lbmsg = "";
            for (int i = j * 10; i < j * 10 + 10; i++) {
                if (i < lb.size()) {
                    lbmsg += "**#" + (i + 1) + "** `" + lb.get(i).getName() + "` — " + lb.get(i).getReputation() + "\n";
                }
            }
            reply.setDescription(lbmsg);

            if (j == 0) {
                embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
            }

            Embed.addPage(embedmsg.getId(), reply);
        }
    }
}
