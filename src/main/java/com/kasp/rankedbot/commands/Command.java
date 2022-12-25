package com.kasp.rankedbot.commands;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.perms.Perms;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Command{

    private String command;
    private String usage;
    private String[] aliases;
    private String description;
    private CommandSubsystem subsystem;

    public Command (String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        System.out.println(command + " command successfully loaded");
        this.command = command;
        this.usage = usage;
        this.aliases = aliases;
        this.description = description;
        this.subsystem = subsystem;
    }

    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        System.out.println("Something went wrong...");
    }

    public String getCommand() {
        return command;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    public String[] getPermissions() {
        return Perms.getPerm(command).split(",");
    }

    public CommandSubsystem getSubsystem() {
        return subsystem;
    }
}
