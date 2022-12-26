package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ForceRenameCmd extends Command {
    public ForceRenameCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");


        if (!Player.isRegistered(ID)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-registered"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ign = args[2];
        ign = ign.replaceAll(" ", "").trim();

        Player player = PlayerCache.getPlayer(ID);
        player.setIgn(ign);
        player.fix();

        Embed reply = new Embed(EmbedType.SUCCESS, "", "You have registered " + guild.getMemberById(ID).getAsMention() + " as `" + ign + "`", 1);
        msg.replyEmbeds(reply.build()).queue();
    }
}
