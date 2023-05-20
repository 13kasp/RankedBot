package com.kasp.rankedbot.listener;

import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.instance.Party;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PartyCache;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PartyInviteButton extends ListenerAdapter {

    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButton().getId().startsWith("rankedbot-pinvitation-")) {
            String players[] = event.getButton().getId().replace("rankedbot-pinvitation-", "").split("=");
            Player leader = PlayerCache.getPlayer(players[0]);
            Player invited = PlayerCache.getPlayer(players[1]);

            if (!event.getMember().getId().equals(invited.getID())) {
                event.reply(Msg.getMsg("not-invited")).setEphemeral(true).queue();
                return;
            }

            if (PartyCache.getParty(invited) != null) {
                event.reply(Msg.getMsg("already-in-party")).setEphemeral(true).queue();
                return;
            }

            if (PartyCache.getParty(leader) == null) {
                event.reply(Msg.getMsg("player-not-in-party")).setEphemeral(true).queue();
                return;
            }

            Party party = PartyCache.getParty(leader);

            if (!party.getInvitedPlayers().contains(invited)) {
                event.reply(Msg.getMsg("not-invited")).setEphemeral(true).queue();
                return;
            }

            if (party.getMembers().size() >= Integer.parseInt(Config.getValue("max-party-members"))) {
                event.reply(Msg.getMsg("this-party-full")).setEphemeral(true).queue();
                return;
            }

            int partyElo = 0;
            for (Player p : party.getMembers()) {
                partyElo += p.getElo();
            }

            if (partyElo + invited.getElo() > Integer.parseInt(Config.getValue("max-party-elo"))) {
                event.reply("You have too much elo to join this party\nParty elo: `" + partyElo + "`\nYour elo: `" + invited.getElo() + "`\nParty elo limit: `" + Config.getValue("max-party-elo") + "`").setEphemeral(true).queue();
                return;
            }

            invited.joinParty(party);

            Embed reply = new Embed(EmbedType.SUCCESS, "", Msg.getMsg("joined-party"), 1);
            event.replyEmbeds(reply.build()).setEphemeral(true).queue();

            Embed embed = new Embed(EmbedType.DEFAULT, "", "<@" + invited.getID() + "> has joined your party", 1);
            event.getChannel().sendMessage("<@" + leader.getID() + ">").setEmbeds(embed.build()).queue();
        }
    }
}
