package com.kasp.rankedbot.commands.theme;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Theme;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.cache.ThemesCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ThemeCmd extends Command {
    public ThemeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[1];

        if (name.equals("list")) {
            String themes = "";
            for (Theme t : ThemesCache.getThemes().values()) {
                themes += "`" + t.getName() + "` ";
            }

            Embed embed = new Embed(EmbedType.DEFAULT, "All themes `(" + ThemesCache.getThemes().size() + ")`", themes, 1);
            msg.replyEmbeds(embed.build()).queue();
        }
        else {
            if (!ThemesCache.containsTheme(name)) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-doesnt-exist"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            Theme theme = ThemesCache.getTheme(name);

            Player player = PlayerCache.getPlayer(sender.getId());

            if (!player.getOwnedThemes().contains(theme)) {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-access-denied"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }

            player.setTheme(theme);

            Embed embed = new Embed(EmbedType.SUCCESS, "", "You have successfully selected theme `" + theme.getName() + "`", 1);
            msg.replyEmbeds(embed.build()).queue();
        }
    }
}
