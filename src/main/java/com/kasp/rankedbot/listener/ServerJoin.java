package com.kasp.rankedbot.listener;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.embed.Embed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ServerJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        if (Player.isRegistered(event.getMember().getId())) {
            Player player = new Player(event.getMember().getId(), null);
            player.fix();

            Embed embed = new Embed(EmbedType.DEFAULT, "Welcome Back", "I've noticed it's not your first time in this server" +
                    "\nI corrected your stats and updated your nickname - you don't need to register again!", 1);

            event.getGuild().getTextChannelById(Config.getValue("alerts-channel")).sendMessage(event.getMember().getAsMention()).setEmbeds(embed.build()).queue();
        }
    }
}
