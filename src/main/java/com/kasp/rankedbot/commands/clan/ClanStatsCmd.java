package com.kasp.rankedbot.commands.clan;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Clan;
import com.kasp.rankedbot.instance.Leaderboard;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ClanStatsCmd extends Command {
    public ClanStatsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String clanName;
        if (args.length == 1)
            if (ClanCache.getClan(PlayerCache.getPlayer(sender.getId())) != null) {
                clanName = ClanCache.getClan(PlayerCache.getPlayer(sender.getId())).getName();
            }
            else {
                Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-in-clan"), 1);
                msg.replyEmbeds(reply.build()).queue();
                return;
            }
        else {
            clanName = args[1];
        }

        if (ClanCache.getClan(clanName) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Clan", Msg.getMsg("clan-doesnt-exist"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Clan clan = ClanCache.getClan(clanName);

        Player clanLeader = clan.getLeader();

        if (new File("RankedBot/clans/theme.png").exists()) {
            Document document = null;
            try {
                document = Jsoup.connect("https://api.mineatar.io/uuid/" + clanLeader.getIgn()).get();
            } catch (IOException ignored) {}

            String skinlink;
            if (document != null) {
                String UUID = document.body().text();
                skinlink = "https://visage.surgeplay.com/full/" + Config.getValue("leaderskin-size") + "/" + UUID;
            }
            else {
                skinlink = "https://visage.surgeplay.com/full/" + Config.getValue("leaderskin-size") + "/069a79f4-44e9-4726-a5be-fca90e38aaf5";
            }

            try {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("RankedBot/fonts/stats.otf")));

                BufferedImage image;
                if (new File("RankedBot/clans/" + clan.getName() + "/theme.png").exists()) {
                    image = ImageIO.read(new File("RankedBot/clans/" + clan.getName() + "/theme.png").toURI().toURL());
                }
                else {
                    image = ImageIO.read(new File("RankedBot/clans/theme.png").toURI().toURL());
                }

                BufferedImage icon;
                if (new File("RankedBot/clans/" + clan.getName() + "/icon.png").exists()) {
                    icon = ImageIO.read(new File("RankedBot/clans/" + clan.getName() + "/icon.png").toURI().toURL());
                }
                else {
                    icon = ImageIO.read(new File("RankedBot/clans/icon.png").toURI().toURL());
                }

                BufferedImage skin;
                try {
                    skin = ImageIO.read(new URL(skinlink));
                } catch (Exception e) {
                    Embed embed = new Embed(EmbedType.ERROR, "Something went wrong...", "Please try executing this command again", 1);
                    msg.replyEmbeds(embed.build()).queue();
                    return;
                }

                Graphics2D gfx = (Graphics2D) image.getGraphics();

                drawText(gfx, "reputation", clan.getReputation() + "");
                drawText(gfx, "name", clan.getName());
                drawText(gfx, "description", clan.getDescription());

                // icon
                gfx.drawImage(icon, Integer.parseInt(Config.getValue("icon-pixels").split(",")[0]), Integer.parseInt(Config.getValue("icon-pixels").split(",")[1]), null);

                List<Clan> clanLB = new ArrayList<>(Leaderboard.getClansLeaderboard());

                drawText(gfx, "ranking", "#" + (clanLB.indexOf(clan) + 1));
                drawText(gfx, "xp", clan.getXp() + "");
                drawText(gfx, "level", clan.getLevel().getLevel() + "");

                int allElo = 0;
                int allGold = 0;
                for (Player p : clan.getMembers()) {
                    allElo += p.getElo();
                    allGold += p.getGold();
                }

                drawText(gfx, "allelo", allElo + "");
                drawText(gfx, "allgold", allGold + "");

                drawText(gfx, "members", clan.getMembers().size() + "/" + Config.getValue("l" + clan.getLevel().getLevel()));
                drawText(gfx, "invited", clan.getInvitedPlayers().size() + "");

                drawText(gfx, "cwplayed", (clan.getWins() + clan.getLosses()) + "");
                drawText(gfx, "cwwins", clan.getWins() + "");
                drawText(gfx, "cwlosses", clan.getLosses() + "");

                double tempLosses = clan.getLosses();
                if (clan.getLosses() < 1) {
                    tempLosses = 1;
                }

                DecimalFormat f = new DecimalFormat("#.##");

                drawText(gfx, "cwwlr", f.format(clan.getWins() / tempLosses));

                // leader ign
                drawText(gfx, "leaderign", clanLeader.getIgn());

                // leader skin
                gfx.drawImage(skin, Integer.parseInt(Config.getValue("leaderskin-pixels").split(",")[0]), Integer.parseInt(Config.getValue("leaderskin-pixels").split(",")[1]), null);

                // finish
                gfx.dispose();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", stream);

                channel.sendFile(stream.toByteArray(), "stats.png").queue();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void drawText(Graphics2D gfx, String configString, String value) {
        gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue(configString + "-size"))));
        gfx.setColor(new Color(Integer.parseInt(Config.getValue(configString + "-color").split(",")[0]),
                Integer.parseInt(Config.getValue(configString + "-color").split(",")[1]),
                Integer.parseInt(Config.getValue(configString + "-color").split(",")[2])));
        gfx.drawString(value + "", Integer.parseInt(Config.getValue(configString + "-pixels").split(",")[0]), Integer.parseInt(Config.getValue(configString + "-pixels").split(",")[1]));
    }
}
