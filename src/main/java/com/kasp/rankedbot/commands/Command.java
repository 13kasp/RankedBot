package com.kasp.rbw3.commands;

import com.kasp.rbw3.perms.Perms;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class Command{

    private String command;
    private String usage;
    private String[] aliases;
    private String description;
    private String subsystem;

    public Command (String command, String usage, String[] aliases, String description, String subsystem) {
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

    public String getCommad() {
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

    public String getSubsystem() {
        return subsystem;
    }
}
