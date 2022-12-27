package com.kasp.rankedbot.commands.server;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Game;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.ServerStats;
import com.kasp.rankedbot.instance.cache.GamesCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SaveDataCmd extends Command {
    public SaveDataCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        for (Player p : PlayerCache.getPlayers().values()) {
            Player.writeFile(p.getID(), null);
        }

        for (Game g : GamesCache.getGames().values()) {
            Game.writeFile(g);
        }

        ServerStats.save();

        Embed reply = new Embed(EmbedType.ERROR, "Data saved", "All players and games data has been saved", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
