package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.cache.PlayerCache;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TransferGoldCmd extends Command {
    public TransferGoldCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 3) {
            Embed reply = new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]", "");

        if (ID.equals(sender.getId())) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-player"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (PlayerCache.getPlayer(ID) == null) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("invalid-player"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player player = PlayerCache.getPlayer(sender.getId());

        int gold = Integer.parseInt(args[2]);

        if (gold < 1) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("too-little-gold"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (player.getGold() < gold) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("not-enough-gold"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        Player getter = PlayerCache.getPlayer(ID);

        getter.setGold(getter.getGold() + gold);
        player.setGold(player.getGold() - gold);

        Embed reply = new Embed(EmbedType.SUCCESS, "", "<@" + player.getID() + "> has sent you `" + gold + "` GOLD\nYou now have `" + getter.getGold() + "` GOLD in total", 1);
        msg.reply("<@" + ID + ">").setEmbeds(reply.build()).queue();
    }
}
