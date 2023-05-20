package com.kasp.rankedbot.commands.server;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.commands.CommandManager;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;

public class HelpCmd extends Command {

    public HelpCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            msg.replyEmbeds(new Embed(EmbedType.ERROR, "Invalid Arguments", Msg.getMsg("wrong-usage").replaceAll("%usage%", getUsage()), 1).build()).queue();
            return;
        }

        ArrayList<Command> commands = new ArrayList<>(CommandManager.getAllCommands());

        if (args.length == 1) {
            Embed reply = new Embed(EmbedType.DEFAULT, "Help Subsystems", "Use `=help <subsystem>` to view the commands of a sub system", 1);

            for (CommandSubsystem s : CommandSubsystem.values()) {
                reply.addField("• " + s.toString().toLowerCase() + " sub-system", "use `=help " + s.toString().toLowerCase() + "`", false);
            }

            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (args.length == 2) {
            CommandSubsystem subsystem;

            try {
                subsystem = CommandSubsystem.valueOf(args[1].toUpperCase());
            } catch (Exception e) {
                String subsystems = "";
                for (CommandSubsystem s :CommandSubsystem.values()) {
                    subsystems += "`" + s + "` ";
                }
                Embed embed = new Embed(EmbedType.ERROR, "Error", "This subsystem does not exist\nAvailable subsystems: " + subsystems, 1);
                msg.replyEmbeds(embed.build()).queue();
                return;
            }

            ArrayList<Command> subsystemCmds = new ArrayList<>();

            for (Command cmd : commands) {
                if (cmd.getSubsystem() == subsystem)
                    subsystemCmds.add(cmd);
            }

            Message embedmsg = msg.replyEmbeds(new EmbedBuilder().setTitle("loading...").build()).complete();

            for (int j = 0; j < Math.ceil(subsystemCmds.size()); j+=3) {

                Embed reply = new Embed(EmbedType.DEFAULT, "All commands in sub-system: " + subsystem, "", (int) Math.ceil(subsystemCmds.size() / 3.0));

                for (int i = 0; i < 3; i++) {
                    if (i + j < subsystemCmds.size()) {

                        String aliases = "";
                        String permissions = "";

                        for (String s : subsystemCmds.get(i + j).getAliases())
                            aliases += "`" + s + "` ";

                        for (String s : subsystemCmds.get(i + j).getPermissions()) {
                            if (s.equals("everyone"))
                                permissions = "@everyone";
                            else
                                permissions += guild.getRoleById(s).getAsMention();
                        }

                        reply.addField("• " + subsystemCmds.get(i + j).getCommand(), subsystemCmds.get(i + j).getDescription() +
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
