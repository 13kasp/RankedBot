package com.kasp.rankedbot.commands.utilities;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.GameMap;
import com.kasp.rankedbot.instance.cache.MapCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class AddMapCmd extends Command {
    public AddMapCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 5) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[1];
        String height = args[2];
        String team1 = args[3];
        String team2 = args[4];

        if (MapCache.containsMap(name)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("map-already-exists"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        GameMap.createFile(name, height, team1, team2);
        new GameMap(name);

        Embed embed = new Embed(EmbedType.SUCCESS, "✅ successfully added `" + name + "` map", "", 1);
        embed.addField("Height Limit:", height, true);
        embed.addField("Teams:", "`" + team1 + "` vs `" + team2 + "`", true);

        msg.replyEmbeds(embed.build()).queue();
    }
}
