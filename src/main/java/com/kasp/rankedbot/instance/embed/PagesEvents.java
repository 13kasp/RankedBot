package com.kasp.rankedbot.instance.embed;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PagesEvents extends ListenerAdapter {

    public void onButtonClick(ButtonClickEvent event) {

        if (event.getButton().getId().startsWith("rankedbot-page-")) {
            Message msg = event.getMessage();
            int number = Integer.parseInt(event.getButton().getId().replace("rankedbot-page-", ""));

            if (number <= -1) {
                event.reply("you're already on the first page").setEphemeral(true).queue();

                return;
            }

            if (Embed.embedPages.get(msg.getId()).size() <= number) {
                event.reply("you're already on the last page").setEphemeral(true).queue();

                return;
            }

            event.deferEdit().queue();

            updatePage(msg, number);
        }
    }

    private void updatePage(Message msg, int number) {
        Embed embed = Embed.embedPages.get(msg.getId()).get(number);

        embed.setCurrentPage(number);
        msg.editMessageEmbeds(embed.build()).setActionRow(Embed.createButtons(embed.getCurrentPage())).queue();
    }
}
