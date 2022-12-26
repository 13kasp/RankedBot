package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Leaderboard;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class LeaderboardCmd extends Command {
    public LeaderboardCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        NumberFormat formatter = new DecimalFormat("#0");
        Statistic statistic = Statistic.ELO;
        if (args.length > 1) {
            statistic = Statistic.valueOf(args[1].toUpperCase());
        }

        List<String> lb = Leaderboard.getLeaderboard(statistic);

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

        for (int j = 0; j < Math.ceil(lb.size()); j+=10) {

            Embed reply = new Embed(EmbedType.DEFAULT, statistic + " Leaderboard", "", (int) Math.ceil(lb.size() / 10.0));

            for (int i = 1; i <= 10; i++) {
                if (i + j < lb.size()) {

                    for (int l = i + j; l <= i + j + 10; l++) {
                        String lbmsg = "";
                        if (l < lb.size()) {
                            String[] values = lb.get(i).split("=");
                            lbmsg += "**#" + i + "** `" + values[0] + "` — " + formatter.format(Double.parseDouble(values[1])) + "\n";
                        }
                        reply.setDescription(lbmsg);
                    }
                }
            }

            if (j == 0) {
                embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
            }

            Embed.addPage(embedmsg.getId(), reply);
        }
    }
}
