package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Theme;
import com.kasp.rankedbot.instance.cache.ClanCache;
import com.kasp.rankedbot.instance.cache.LevelCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.cache.ThemeCache;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class StatsCmd extends Command {
    public StatsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID;
        if (args.length == 1)
            ID = sender.getId();
        else {
            if (args[1].equals("full"))
                ID = sender.getId();
            else
                ID = args[1].replaceAll("[^0-9]","");
        }

        if (PlayerCache.getPlayer(ID) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Player", Msg.getMsg("invalid-player"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(ID);

        DecimalFormat f = new DecimalFormat("#.##");

        double templosses = 1;
        if (player.getLosses() > 0)
            templosses = player.getLosses();
        
        int games = player.getWins() + player.getLosses();

        if (args.length == 2 && args[1].equals("full")) {
            msg.replyEmbeds(statsFulLEmbed(player, games, templosses).build()).queue();
            return;
        }

        if (Boolean.parseBoolean(Config.getValue("s-enabled"))) {
            if (ThemeCache.getTheme("default") != null) {
                String uuid = null;
                try {
                    uuid = new JSONObject(IOUtils.toString(URI.create("https://api.mojang.com/users/profiles/minecraft/" + player.getIgn()), StandardCharsets.UTF_8)).getString("id");
                } catch (Exception ignored) {}

                String skinlink;
                if (uuid != null) {
                    skinlink = "https://visage.surgeplay.com/full/" + Config.getValue("skin-size") + "/" + uuid;
                }
                else {
                    skinlink = "https://visage.surgeplay.com/full/" + Config.getValue("skin-size") + "/75a0352f17b64119a041d0be09701235";
                }

                try {
                    player.fix();

                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("RankedBot/fonts/stats.otf")));

                    BufferedImage image = ImageIO.read(new File("RankedBot/themes/" + player.getTheme().getName() + ".png").toURI().toURL());

                    BufferedImage skin;
                    try {
                        skin = ImageIO.read(new URL(skinlink));
                    } catch (Exception e) {
                        Embed embed = new Embed(EmbedType.ERROR, "Something went wrong...", "Please try executing this command again", 1);
                        msg.replyEmbeds(embed.build()).queue();
                        return;
                    }

                    Graphics2D gfx = (Graphics2D) image.getGraphics();

                    gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("ign-size"))));
                    gfx.setColor(new Color(Integer.parseInt(Config.getValue("ign-color").split(",")[0]),
                            Integer.parseInt(Config.getValue("ign-color").split(",")[1]),
                            Integer.parseInt(Config.getValue("ign-color").split(",")[2])));
                    gfx.drawString(player.getIgn() + "", Integer.parseInt(Config.getValue("ign-pixels").split(",")[0]), Integer.parseInt(Config.getValue("ign-pixels").split(",")[1]));

                    for (Statistic s : Statistic.values()) {
                        if (s == Statistic.ID) {
                            continue;
                        }
                        gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue(s + "-size"))));
                        gfx.setColor(new Color(Integer.parseInt(Config.getValue(s + "-color").split(",")[0]),
                                Integer.parseInt(Config.getValue(s + "-color").split(",")[1]),
                                Integer.parseInt(Config.getValue(s + "-color").split(",")[2])));
                        if (s == Statistic.WLR || s == Statistic.KDR){
                            gfx.drawString(f.format(player.getStatistic(s)) + "", Integer.parseInt(Config.getValue(s + "-pixels").split(",")[0]), Integer.parseInt(Config.getValue(s + "-pixels").split(",")[1]));
                        }
                        else {
                            gfx.drawString((int) player.getStatistic(s) + "", Integer.parseInt(Config.getValue(s + "-pixels").split(",")[0]), Integer.parseInt(Config.getValue(s + "-pixels").split(",")[1]));
                        }
                    }

                    // needed xp
                    if (LevelCache.containsLevel(player.getLevel().getLevel()+1)) {
                        gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("needed-xp-size"))));
                        gfx.setColor(new Color(Integer.parseInt(Config.getValue("needed-xp-color").split(",")[0]),
                                Integer.parseInt(Config.getValue("needed-xp-color").split(",")[1]),
                                Integer.parseInt(Config.getValue("needed-xp-color").split(",")[2])));
                        gfx.drawString(LevelCache.getLevel(player.getLevel().getLevel()+1).getNeededXP() + "", Integer.parseInt(Config.getValue("needed-xp-pixels").split(",")[0]), Integer.parseInt(Config.getValue("needed-xp-pixels").split(",")[1]));
                    }

                    // theme
                    gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("theme-size"))));
                    gfx.setColor(new Color(Integer.parseInt(Config.getValue("theme-color").split(",")[0]),
                            Integer.parseInt(Config.getValue("theme-color").split(",")[1]),
                            Integer.parseInt(Config.getValue("theme-color").split(",")[2])));
                    gfx.drawString(player.getTheme().getName() + "", Integer.parseInt(Config.getValue("theme-pixels").split(",")[0]), Integer.parseInt(Config.getValue("theme-pixels").split(",")[1]));

                    // rbw role
                    Role role = guild.getRoleById(player.getRank().getID());
                    gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("rbw-rank-size"))));
                    gfx.setColor(role.getColor());
                    gfx.drawString(role.getName(), Integer.parseInt(Config.getValue("rbw-rank-pixels").split(",")[0]), Integer.parseInt(Config.getValue("rbw-rank-pixels").split(",")[1]));

                    // banned
                    if (player.isBanned()) {
                        gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("banned-size"))));
                        gfx.setColor(new Color(Integer.parseInt(Config.getValue("banned-color").split(",")[0]),
                                Integer.parseInt(Config.getValue("banned-color").split(",")[1]),
                                Integer.parseInt(Config.getValue("banned-color").split(",")[2])));
                        gfx.drawString("BANNED" + "", Integer.parseInt(Config.getValue("banned-pixels").split(",")[0]), Integer.parseInt(Config.getValue("banned-pixels").split(",")[1]));
                    }

                    // clan
                    if (ClanCache.getClan(player) != null) {
                        gfx.setFont(new Font(Config.getValue("s-text-font"), Font.PLAIN, Integer.parseInt(Config.getValue("clan-size"))));
                        gfx.setColor(new Color(Integer.parseInt(Config.getValue("clan-color").split(",")[0]),
                                Integer.parseInt(Config.getValue("clan-color").split(",")[1]),
                                Integer.parseInt(Config.getValue("clan-color").split(",")[2])));
                        gfx.drawString(ClanCache.getClan(player).getName(), Integer.parseInt(Config.getValue("clan-pixels").split(",")[0]), Integer.parseInt(Config.getValue("clan-pixels").split(",")[1]));
                    }

                    // skin
                    gfx.drawImage(skin, Integer.parseInt(Config.getValue("skin-pixels").split(",")[0]), Integer.parseInt(Config.getValue("skin-pixels").split(",")[1]), null);

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

                return;
            }
        }

        msg.replyEmbeds(statsFulLEmbed(player, games, templosses).build()).queue();
    }

    private Embed statsFulLEmbed(Player player, int games, double templosses) {
        DecimalFormat f = new DecimalFormat("#.##");

        Embed embed = new Embed(EmbedType.DEFAULT, player.getIgn() + "'s Stats", "",1);

        embed.addField("__General Stats__",
                "> `Elo` " + player.getElo() + " **(#" + player.getPlacement(Statistic.ELO) + ")**" +
                        "\n> ┗ `Peak` " + player.getPeakElo() + " **(#" + player.getPlacement(Statistic.PEAKELO) + ")**" +
                        "\n> `Games` " + games + " **(#" + player.getPlacement(Statistic.GAMES) + ")**" +
                        "\n> `WLR` " + f.format(player.getWins() / templosses) + " **(#" + player.getPlacement(Statistic.WLR) + ")**" +
                        "\n> `Mvp` " + player.getMvp() + " **(#" + player.getPlacement(Statistic.MVP) + ")**" +
                        "\n> `Strikes` " + player.getStrikes() + " **(#" + player.getPlacement(Statistic.STRIKES) + ")**" +
                        "\n> `Scored` " + player.getScored() + " **(#" + player.getPlacement(Statistic.SCORED) + ")**", false);

        embed.addField("__Games Stats__",
                "> **`Wins`** " + player.getWins() + " **(#" + player.getPlacement(Statistic.WINS) + ")**" +
                        "\n> `Winstreak` " + player.getWinStreak() + " **(#" + player.getPlacement(Statistic.WINSTREAK) + ")**" +
                        "\n> ┗ `Highest` " + player.getHighestWS() + " **(#" + player.getPlacement(Statistic.HIGHESTWS) + ")**" +
                        "\n> **`Losses`** " + player.getLosses() + " **(#" + player.getPlacement(Statistic.LOSSES) + ")**" +
                        "\n> `Losestreak` " + player.getLossStreak() + " **(#" + player.getPlacement(Statistic.LOSSSTREAK) + ")**" +
                        "\n> ┗ `Highest` " + player.getHighestLS() + " **(#" + player.getPlacement(Statistic.HIGHESTLS) + ")**", false);

        embed.addField("__K/D Stats__",
                "> `Kills` " + player.getKills() + " **(#" + player.getPlacement(Statistic.KILLS) + ")**" +
                        "\n> `Deaths` " + player.getDeaths() + " **(#" + player.getPlacement(Statistic.DEATHS) + ")**" +
                        "\n> `KDR` " + player.getHighestWS() + " **(#" + player.getPlacement(Statistic.KDR) + ")**", false);

        if (player.getOwnedThemes().get(0) == null) {
            embed.addField("__Other Stats__",
                    "> `Gold` " + player.getGold() + " **(#" + player.getPlacement(Statistic.GOLD) + ")**" +
                            "\n> `Level` " + player.getLevel().getLevel() + " **(#" + player.getPlacement(Statistic.LEVEL) + ")**" +
                            "\n> `Xp` " + player.getXp() + " **(#" + player.getPlacement(Statistic.XP) + ")**", false);
        }
        else {
            StringBuilder themes = new StringBuilder();
            for (Theme t : player.getOwnedThemes()) {
                themes.append(t.getName() + " ");
            }

            embed.addField("__Other Stats__",
                    "> `Gold` " + player.getGold() + " **(#" + player.getPlacement(Statistic.GOLD) + ")**" +
                            "\n> `Level` " + player.getLevel().getLevel() + " **(#" + player.getPlacement(Statistic.LEVEL) + ")**" +
                            "\n> `Xp` " + player.getXp() + " **(#" + player.getPlacement(Statistic.XP) + ")**" +
                            "\n> `Selected theme` " + player.getTheme().getName() +
                            "\n> `Owned themes` " + themes, false);
        }

        return embed;
    }
}
