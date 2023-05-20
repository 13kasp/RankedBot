package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Leaderboard;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
            try {
                statistic = Statistic.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                String stats = "";
                for (Statistic s : Statistic.values()) {
                    stats += "`" + s + "` ";
                }
                Embed embed = new Embed(EmbedType.ERROR, "Error", "This statistic does not exist\nAvailable stats: " + stats, 1);
                msg.replyEmbeds(embed.build()).queue();
                return;
            }
        }

        List<String> lb = new ArrayList<>(Leaderboard.getLeaderboard(statistic));

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

        for (int j = 0; j < Math.ceil(lb.size() / 10.0); j++) {

            Embed reply = new Embed(EmbedType.DEFAULT, statistic + " Leaderboard", "", (int) Math.ceil(lb.size() / 10.0));

            String lbmsg = "";
            for (int i = j * 10; i < j * 10 + 10; i++) {
                if (i < lb.size()) {
                    String[] values = lb.get(i).split("=");
                    Player p = PlayerCache.getPlayer(values[0]);
                    lbmsg += "**#" + (i + 1) + "** `" + p.getIgn() + "` — " + formatter.format(Double.parseDouble(values[1])) + "\n";
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
