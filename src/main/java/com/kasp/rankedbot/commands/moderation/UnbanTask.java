package com.kasp.rankedbot.commands.moderation;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.embed.Embed;

import java.time.LocalDateTime;
import java.util.Objects;

public class UnbanTask {

    public static void checkAndUnbanPlayers() {
        String unbanned = "";

        for (Player p : PlayerCache.getPlayers().values()) {
            if (p.isBanned()) {
                if (p.getBannedTill() == null || p.getBannedTill().isBefore(LocalDateTime.now())) {
                    p.unban();

                    unbanned += "<@" + p.getID() + "> (" + p.getIgn() + ") ";
                }
            }
        }

        if (unbanned != "") {
            if (!Objects.equals(Config.getValue("ban-channel"), null)) {
                Embed embed = new Embed(EmbedType.DEFAULT, "Unbanned some players `(auto)`:", unbanned, 1);
                RankedBot.getGuild().getTextChannelById(Config.getValue("ban-channel")).sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}
