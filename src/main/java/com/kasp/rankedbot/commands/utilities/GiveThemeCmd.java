package com.kasp.rankedbot.commands.utilities;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Theme;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.cache.ThemeCache;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class GiveThemeCmd extends Command {
    public GiveThemeCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String name = args[2];

        if (!ThemeCache.containsTheme(name)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("theme-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Theme theme = ThemeCache.getTheme(name);

        String ID = args[1].replaceAll("[^0-9]","");

        Player player = PlayerCache.getPlayer(ID);

        if (player.getOwnedThemes().contains(theme)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("already-has-theme"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        player.getOwnedThemes().add(theme);

        Embed embed = new Embed(EmbedType.SUCCESS, "", "You have given " + guild.getMemberById(ID).getAsMention() + " `" + theme.getName() + "` theme", 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
