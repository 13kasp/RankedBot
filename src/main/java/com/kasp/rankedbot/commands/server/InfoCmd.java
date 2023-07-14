package com.kasp.rankedbot.commands.server;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.GameState;
import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Queue;
import com.kasp.rankedbot.instance.cache.GameCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.cache.QueueCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class InfoCmd extends Command {
    public InfoCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.DEFAULT, "Server Info", "Ranked Bot v" + RankedBot.version + " by `13kasp`\nget free: https://discord.gg/5DdEvfRwz2", 1);
        embed.addField("Players", "`" + PlayerCache.getPlayers().size() + "` registered | `" + guild.getMemberCount() + "` total", false);
        int qing = 0;
        for (Queue q : QueueCache.getQueues().values()) {
            qing += q.getPlayers().size();
        }
        int playing = 0;
        for (Game g : GameCache.getGames().values()) {
            if (g.getState() == GameState.PLAYING) {
                playing += g.getPlayers().size();
            }
        }

        embed.addField("Currently playing", "`" + qing + "` queueing | `" + playing + "` playing", false);
        embed.addField("RAM Usage", "`" + Runtime.getRuntime().freeMemory() / 1048576 + "`/`" + Runtime.getRuntime().maxMemory() / 1048576 + "` MB", false);
        msg.replyEmbeds(embed.build()).queue();
    }
}
