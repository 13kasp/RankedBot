package com.kasp.rbw3.commands.playerCmds;

import com.kasp.rbw3.commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Rename extends Command {
    public Rename(String command, String usage, String[] aliases, String description, String subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        System.out.println("rename cmd executed");
    }
}
