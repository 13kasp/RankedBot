package com.kasp.rankedbot.commands.utilities;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Rank;
import com.kasp.rankedbot.instance.cache.RankCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

public class AddRankCmd extends Command {
    public AddRankCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 7) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Role role = null;
        String ID = args[1].replaceAll("[^0-9]","");
        try {role = guild.getRoleById(ID);}catch (Exception ignored){}

        if (role == null) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("invalid-role"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        if (RankCache.containsRank(ID)) {
            Embed error = new Embed(EmbedType.ERROR, "", Msg.getMsg("rank-already-exists"), 1);
            msg.replyEmbeds(error.build()).queue();
            return;
        }

        String startingElo = args[2];
        String endingElo = args[3];
        String winElo = args[4];
        String loseElo = args[5];
        String mvpElo = args[6];

        Rank.createFile(ID, startingElo, endingElo, winElo, loseElo, mvpElo);
        new Rank(ID);

        Embed success = new Embed(EmbedType.SUCCESS, "✅ successfully added `" + role.getName() + "` rank", "", 1);
        success.addField("Role:", role.getAsMention(), true);
        success.addField("Starting elo:", startingElo, true);
        success.addField("Ending elo:", endingElo, true);
        success.addField("Win elo:", "+" + winElo, true);
        success.addField("Lose elo:", "-" + loseElo, true);
        success.addField("Mvp Elo:", mvpElo, true);

        msg.replyEmbeds(success.build()).queue();
    }
}
