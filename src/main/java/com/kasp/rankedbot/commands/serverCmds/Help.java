package com.kasp.rbw3.commands.serverCmds;

import com.kasp.rbw3.commands.Command;
import com.kasp.rbw3.commands.CommandManager;
import com.kasp.rbw3.config.Config;
import com.kasp.rbw3.classes.embed.Embed;
import com.kasp.rbw3.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;

public class Help extends Command {

    public Help(String command, String usage, String[] aliases, String description, String subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            msg.replyEmbeds(new Embed("error", "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
            return;
        }

        ArrayList<Command> commands = new ArrayList<>(CommandManager.getAllCommands());

        if (args.length == 1) {
            Embed reply = new Embed("default", "Help Subsystems", "Use `=help <subsystem>` to view the commands of a sub system", 1);

            ArrayList<String> subsystems = new ArrayList<>();

            for (Command cmd : commands) {
                if (!subsystems.contains(cmd.getSubsystem())) {
                    subsystems.add(cmd.getSubsystem());
                }
            }

            for (String s : subsystems) {
                reply.addField("• " + s + " sub-system", "use `=help " + s + "`", false);
            }

            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (args.length == 2) {
            String aliases = "";
            String permissions = "";

            String subsystem = args[1];

            ArrayList<Command> subsystemCmds = new ArrayList<>();

            for (Command cmd : commands) {
                if (cmd.getSubsystem().equalsIgnoreCase(subsystem))
                    subsystemCmds.add(cmd);
            }

            Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

            for (int j = 0; j < Math.ceil(subsystemCmds.size()); j+=3) {

                Embed reply = new Embed("default", "All commands in sub-system: " + subsystem, "", (int) Math.ceil(subsystemCmds.size() / 3.0));

                for (int i = 0; i < 3; i++) {
                    if (i + j < subsystemCmds.size()) {

                        for (String s : subsystemCmds.get(i + j).getAliases())
                            aliases += "`" + s + "` ";

                        for (String s : subsystemCmds.get(i + j).getPermissions()) {
                            if (s.equals("everyone"))
                                permissions = "@everyone";
                            else
                                permissions += guild.getRoleById(s).getAsMention();
                        }

                        reply.addField("• " + subsystemCmds.get(i + j).getCommad(), subsystemCmds.get(i + j).getDescription() +
                                "\n> Usage: `" + Config.getValue("prefix") + subsystemCmds.get(i + j).getUsage() +
                                "`\n> Aliases: " + aliases +
                                "\n> Permissions: " + permissions + "\n", false);
                    }
                }
                reply.addField("Note", "`<something>` - required\n`[something]` - optional", false);

                if (j == 0) {
                    embedmsg.editMessageEmbeds(reply.build()).setActionRow(Embed.createButtons(reply.getCurrentPage())).queue();
                }

                Embed.addPage(embedmsg.getId(), reply);
            }
        }
    }
}
