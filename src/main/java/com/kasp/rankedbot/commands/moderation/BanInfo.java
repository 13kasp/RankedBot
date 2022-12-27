package com.kasp.rankedbot.commands.moderation;

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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class BanInfo extends Command {
    public BanInfo(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        Player player = PlayerCache.getPlayer(ID);

        if (!player.isBanned()) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("player-not-banned"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        long diffDays = ChronoUnit.DAYS.between(LocalDateTime.now(), player.getBannedTill());
        long diffHours = ChronoUnit.HOURS.between(LocalDateTime.now(), player.getBannedTill());
        long diffMins = ChronoUnit.MINUTES.between(LocalDateTime.now(), player.getBannedTill());

        String desc = "Unbanned On `" + sdf.format(player.getBannedTill()) + " GMT`\nTime till unbanned `≈ " + diffDays + "d / " + diffHours + "h / " + diffMins + "m`\n**Reason: **" + player.getBanReason();

        Embed embed = new Embed(EmbedType.DEFAULT, player.getIgn() + "'s Ban Info", desc, 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
