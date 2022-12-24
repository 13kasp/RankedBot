package com.kasp.rankedbot.classes.embed;

import com.kasp.rankedbot.RankedBot;
import com.kasp.rankedbot.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Embed {

    public static HashMap<String, List<Embed>> embedPages = new HashMap<>();

    private int pages;
    private int currentPage = 0;
    private String type;
    private String title;
    private String description;
    private String thumbnailURL;
    private String imageURL;
    private String footer;
    private List<MessageEmbed.Field> fields = new ArrayList<>();

    EmbedBuilder eb = new EmbedBuilder();

    public Embed(String type, String title, String description, int pages) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.pages = pages;

        String[] d = Config.getValue("default").split(",");
        String[] s = Config.getValue("success").split(",");
        String[] e = Config.getValue("error").split(",");

        if (type.equalsIgnoreCase("success")) {eb.setColor(new Color(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])));}
        else if (type.equalsIgnoreCase("error")) {eb.setColor(new Color(Integer.parseInt(e[0]), Integer.parseInt(e[1]), Integer.parseInt(e[2])));}
        else {eb.setColor(new Color(Integer.parseInt(d[0]), Integer.parseInt(d[1]), Integer.parseInt(d[2])));}

        this.footer = Config.getValue("footer").replaceAll("%name%", Config.getValue("server-name")).replaceAll("%version%", RankedBot.version);
    }

    public MessageEmbed build() {
        if (title != null && !title.equals(""))
            if (pages != 1) {
                eb.setTitle(title + " `[Page: " + (currentPage + 1) + "/" + pages + "]`");
            }
            else {
                eb.setTitle(title);
            }
        if (description != null && !description.equals(""))
            eb.setDescription(description);
        if (thumbnailURL != null && !thumbnailURL.equals(""))
            eb.setThumbnail(thumbnailURL);
        if (imageURL != null && !imageURL.equals(""))
            eb.setImage(imageURL);
        if (footer != null && !footer.equals(""))
            eb.setFooter(footer).setTimestamp(OffsetDateTime.now());

        for (MessageEmbed.Field f : fields) {
            eb.addField(f.getName(), f.getValue(), f.isInline());
        }

        fields.clear();

        return eb.build();
    }

    public void addField(String title, String content, boolean inline) {
        fields.add(new MessageEmbed.Field(title, content, inline));
    }

    public List<MessageEmbed.Field> getFields() {
        return fields;
    }

    public static List<Button> createButtons(int currentPage) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.secondary("rankedbot-page-" + (currentPage - 1), "←"));
        buttons.add(Button.secondary("rankedbot-page-" + (currentPage + 1), "→"));
        return buttons;
    }

    public static Embed getPage(String msgID, int page) {
        return embedPages.get(msgID).get(page);
    }

    public static void addPage(String msgID, Embed embed) {
        List<Embed> pages;

        if (embedPages.get(msgID) != null) {
            pages = new ArrayList<>(embedPages.get(msgID));
        }
        else {
            pages = new ArrayList<>();
        }

        pages.add(embed);
        embedPages.put(msgID, pages);
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public EmbedBuilder getEb() {
        return eb;
    }

    public void setEb(EmbedBuilder eb) {
        this.eb = eb;
    }
}
