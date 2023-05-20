package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Leaderboard;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ClanListCmd extends Command {
    public ClanListCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

        List<Clan> clans = new ArrayList<>(ClanCache.getClans().values());
        List<Clan> clanLB = new ArrayList<>(Leaderboard.getClansLeaderboard());

        for (int j = 0; j < Math.ceil(clans.size()); j+=5) {

            Embed reply = new Embed(EmbedType.DEFAULT, "All clans", "", (int) Math.ceil(clans.size() / 5.0));

            for (int i = 0; i < 5; i++) {
                if (i + j < ClanCache.getClans().size()) {
                    reply.addField("• " + clans.get(i + j).getName() + " `[" + clans.get(i + j).getMembers().size() + "/" + Config.getValue("l" + clans.get(i + j).getLevel().getLevel()) + "]`",
                            clans.get(i + j).getDescription() +
                            "\n> Private: `" + clans.get(i + j).isPrivate() + "`" +
                            "\n> Leader: <@" + clans.get(i + j).getLeader().getID() + ">" +
                            "\n> Reputation: **" + clans.get(i + j).getReputation() + "** `[#" + (clanLB.indexOf(clans.get(i + j)) + 1) + "]`\n", false);
                }
            }

            reply.addField("Note", "You can view more stats ab a certain clan\nby using `=cstats <name>` and `=cinfo <name>`", false);

            if (j == 0) {
                embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
            }

            Embed.addPage(embedmsg.getId(), reply);
        }
    }
}
