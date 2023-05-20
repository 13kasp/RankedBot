package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;

public class ClanSettingsCmd extends Command {
    public ClanSettingsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        String[] settings = {"private", "eloreq", "description", "icon", "theme"};
        String[] settingsvalue = {"true/false", "number", "text", "attached 135x135 image", "attached 960x540 image"};
        String[] settingsdesc = {"make your clan private - only allow invited players to join\nor public - anyone will be able to join your clan",
                                "change the required min. elo to join your clan - only works if your clan is set to public",
                                "change the description of your clan",
                                "change the icon of your clan",
                                "change the =cstats theme of your clan"};

        if (args.length < 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        if (ClanCache.getClan(player) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(player);

        if (clan.getLeader() != player) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-clan-leader"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String setting = args[1];

        if (!Arrays.asList(settings).contains(setting)) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-setting"), 1);
            msg.replyEmbeds(reply.build()).queue();
            Embed embed = new Embed(EmbedType.DEFAULT, "Available settings", "", 1);
            for (int i = 0; i < settings.length; i++) {
                embed.addField(settings[i], "Value - `" + settingsvalue[i] + "`\n" + settingsdesc[i], false);
            }
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        String value = "";

        int settingIndex = Arrays.asList(settings).indexOf(setting);

        try {
            if (setting.equals("private")) {
                value = args[2];
                clan.setPrivate(Boolean.parseBoolean(args[2]));
            }
            else if (setting.equals("eloreq")) {
                value = args[2];
                clan.setEloJoinReq(Integer.parseInt(args[2]));
            }
            else if (setting.equals("description")) {
                if (msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim().length() > Integer.parseInt(Config.getValue("clan-desc-max"))) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("desc-too-long"), 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                clan.setDescription(msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim());
                value = msg.getContentRaw().replace(args[1], "").replace(args[0], "").trim();
            }
            else if (setting.equals("icon")) {
                if (clan.getLevel().getLevel() < Integer.parseInt(Config.getValue("allow-setting-icon"))) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "Your clan needs to reach level " + Config.getValue("allow-setting-icon") + " for you to be able to set the icon", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (!msg.getAttachments().get(0).isImage()) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "The attached file has to be an image", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (msg.getAttachments().size() < 1) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "You have to attach a `135x135` image as your theme", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (!msg.getAttachments().get(0).getFileName().equals("icon.png")) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "The image file has to be named `icon.png`", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (msg.getAttachments().get(0).getWidth() != 135 || msg.getAttachments().get(0).getHeight() != 135) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "You have to attach a `135x135` image as your theme", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }

                msg.getAttachments().get(0).downloadToFile("RankedBot/clans/" + clan.getName() + "/" + msg.getAttachments().get(0).getFileName());

                value = "icon.png";
            }
            else if (setting.equals("theme")) {
                if (clan.getLevel().getLevel() < Integer.parseInt(Config.getValue("allow-setting-theme"))) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "Your clan needs to reach level " + Config.getValue("allow-setting-theme") + " for you to be able to set the icon", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (!msg.getAttachments().get(0).isImage()) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "The attached file has to be an image", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (msg.getAttachments().size() < 1) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "You have to attach a `960x540` image as your theme", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (!msg.getAttachments().get(0).getFileName().equals("theme.png")) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "The image file has to be named `theme.png`", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }
                if (msg.getAttachments().get(0).getWidth() != 960 || msg.getAttachments().get(0).getHeight() != 540) {
                    Embed reply = new Embed(EmbedType.ERROR, "Error", "You have to attach a `960x540` image as your theme", 1);
                    msg.replyEmbeds(reply.build()).queue();
                    return;
                }

                msg.getAttachments().get(0).downloadToFile("RankedBot/clans/" + clan.getName() + "/" + msg.getAttachments().get(0).getFileName());

                value = "theme.png";
            }
        } catch (Exception e) {
            Embed embed = new Embed(EmbedType.ERROR, "Error", "Something went wrong... please make sure that youre setting `" + settingsvalue[settingIndex] + "` as your value", 1);
            msg.replyEmbeds(embed.build()).queue();
            return;
        }

        Embed embed = new Embed(EmbedType.SUCCESS, "Successfully updated the settings", "Successfully set `" + value + "` as your clan's `" + setting + "`", 1);
        msg.replyEmbeds(embed.build()).queue();
    }
}
