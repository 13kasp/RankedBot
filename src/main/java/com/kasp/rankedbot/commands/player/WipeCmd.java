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

import java.io.File;

public class WipeCmd extends Command {
    public WipeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (args[1].equals("everyone")) {
            File[] filesList = new File("RankedBot/players").listFiles();
            double time = filesList.length / 15.0;

            Embed reply = new Embed(EmbedType.DEFAULT, "Resetting everyone's stats...", "`Check console for more details`", 1);
            reply.addField("WARNING", "please do not use any other cmd during the reset\nit might result into errors / slower resetting", false);
            reply.addField("Estimated time", time + " second(s) `(" + filesList.length + " players)`", false);
            reply.addField("Reset by:", sender.getAsMention(), true);
            msg.replyEmbeds(reply.build()).queue();
            long start = System.currentTimeMillis();

            for (File file : filesList) {
                Player player = PlayerCache.getPlayer(file.getName().replaceAll(".yml", ""));
                player.wipe();
                if (guild.getMemberById(player.getID()) != null) {
                    player.fix();
                }
                System.out.println("[=wipe] successfully reset " + player.getIgn() + " (" + player.getID() + ")");
            }

            long end = System.currentTimeMillis();
            float elapsedTime = (end - start) / 1000F;

            Embed success = new Embed(EmbedType.SUCCESS, "All stats were successfully reset", "", 1);
            success.addField("Resetting took", "`" + elapsedTime + "` seconds `(" + filesList.length + " players)`", true);
            success.addField("Reset by:", sender.getAsMention(), true);
            msg.replyEmbeds(success.build()).queue();
        }
        else {
            Player player = PlayerCache.getPlayer(args[1].replaceAll("[^0-9]", ""));
            player.wipe();
            player.fix();

            Embed reply = new Embed(EmbedType.SUCCESS, "Stats wiped", Msg.getMsg("successfully-wiped"), 1);
            msg.replyEmbeds(reply.build()).queue();
        }
    }
}
