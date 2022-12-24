package com.kasp.rbw3.commands;

import com.kasp.rbw3.commands.playerCmds.*;
import com.kasp.rbw3.commands.serverCmds.Help;
import com.kasp.rbw3.config.Config;
import com.kasp.rbw3.classes.embed.Embed;
import com.kasp.rbw3.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager extends ListenerAdapter {

    static ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new Register("register", "register <in-game name>", new String[]{}, "Register yourself to start playing", "player"));
        commands.add(new Rename("rename", "rename <in-game name>", new String[]{}, "Change your in-game name", "player"));
        commands.add(new Help("help", "help [sub-system]", new String[]{"info"}, "See the available server commands", "server"));
        commands.add(new Queue("queue", "queue", new String[]{"q"}, "See your game's queue", "game"));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = Config.getValue("prefix");

        Embed noPermsEmbed = new Embed("error", "No Permission", Msg.getMsg("no-perms"), 1);

        String[] args = event.getMessage().getContentRaw().split(" ");
        Guild g = event.getGuild();
        Member m = event.getMember();
        TextChannel c = event.getChannel();
        Message msg = event.getMessage();

        if (msg.getContentRaw().startsWith(prefix)) {
            if (Boolean.parseBoolean(Config.getValue("log-commands"))) {
                System.out.println("[RBW] " + m.getUser() .getAsTag() + " used " + msg.getContentRaw());
            }

            for (Command cmd : commands) {
                String[] aliases = cmd.getAliases();
                boolean isAlias = Arrays.asList(aliases).contains(args[0].toLowerCase().replace(prefix, ""));
                if (args[0].replace(prefix, "").equalsIgnoreCase(cmd.getCommad()) || isAlias) {
                    cmd.execute(args, g, m, c, msg);
                }
            }
        }
    }

    public static ArrayList<Command> getAllCommands() {
        return commands;
    }
}
