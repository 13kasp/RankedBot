package com.kasp.rankedbot.commands;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.commands.game.*;
import com.kasp.rankedbot.commands.map.AddMapCmd;
import com.kasp.rankedbot.commands.map.DeleteMapCmd;
import com.kasp.rankedbot.commands.map.MapsCmd;
import com.kasp.rankedbot.commands.player.*;
import com.kasp.rankedbot.commands.queue.AddQueueCmd;
import com.kasp.rankedbot.commands.queue.DeleteQueueCmd;
import com.kasp.rankedbot.commands.queue.QueuesCmd;
import com.kasp.rankedbot.commands.rank.AddRankCmd;
import com.kasp.rankedbot.commands.rank.DeleteRankCmd;
import com.kasp.rankedbot.commands.rank.RanksCmd;
import com.kasp.rankedbot.commands.server.HelpCmd;
import com.kasp.rankedbot.commands.server.ReloadConfigCmd;
import com.kasp.rankedbot.commands.server.SaveDataCmd;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import com.kasp.rankedbot.perms.Perms;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    static ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new HelpCmd("help", "help [sub-system]", new String[]{"info"}, "See the available server commands", CommandSubsystem.SERVER));
        commands.add(new ReloadConfigCmd("reloadconfig", "reloadconfig", new String[]{"reload", "rc"}, "Reload the config (update values)", CommandSubsystem.SERVER));
        commands.add(new SaveDataCmd("savedata", "savedata", new String[]{"save", "sd"}, "Save player and games data", CommandSubsystem.SERVER));

        commands.add(new RegisterCmd("register", "register <in-game name>", new String[]{}, "Register yourself to start playing", CommandSubsystem.PLAYER));
        commands.add(new RenameCmd("rename", "rename <in-game name>", new String[]{}, "Change your in-game name", CommandSubsystem.PLAYER));
        commands.add(new FixCmd("fix", "fix [ID/mention]", new String[]{"correct"}, "Fix discord roles and nickname depending on the stats", CommandSubsystem.PLAYER));
        commands.add(new ForceRegisterCmd("forceregister", "forceregister <ID/mention> <ign>", new String[]{"freg"}, "Forcefully register a player", CommandSubsystem.PLAYER));
        commands.add(new ForceRenameCmd("forcerename", "forcerename <ID/mention> <new ign>", new String[]{"fren"}, "Forcefully rename a player", CommandSubsystem.PLAYER));
        commands.add(new WipeCmd("wipe", "wipe <ID/mention/\"everyone\">", new String[]{"reset"}, "Reset player's / everyone's stats", CommandSubsystem.PLAYER));
        commands.add(new StatsCmd("stats", "stats [ID/mention/\"full\"]", new String[]{"s", "i"}, "View player's stats", CommandSubsystem.PLAYER));
        commands.add(new LeaderboardCmd("leaderboard", "leaderboard <statistic>", new String[]{"lb"}, "View the leaderbaord for a statistic", CommandSubsystem.PLAYER));
        commands.add(new ModifyCmd("modify", "modify <ID/mention> <statistic> <value>", new String[]{"edit"}, "Modify player's stats", CommandSubsystem.PLAYER));
        commands.add(new ScreenshareCmd("screenshare", "screenshare <ID/mention> <reason>", new String[]{"ss"}, "Screenshare a player", CommandSubsystem.PLAYER));

        commands.add(new QueueCmd("queue", "queue", new String[]{"q"}, "View your game's queue", CommandSubsystem.GAME));
        commands.add(new QueueStats("queuestats", "queuestats", new String[]{"qs"}, "View your game's queue stats", CommandSubsystem.GAME));
        commands.add(new GameInfoCmd("gameinfo", "gameinfo <number>", new String[]{"gi"}, "View info about a game", CommandSubsystem.GAME));
        commands.add(new PickCmd("pick", "pick <ID/mention>", new String[]{"p"}, "Pick a player in your game (if you're a captain)", CommandSubsystem.GAME));
        commands.add(new VoidCmd("void", "void", new String[]{"cleargame", "clear", "cg"}, "Cancel a game if you can't play it anymore", CommandSubsystem.GAME));

        commands.add(new AddQueueCmd("addqueue", "addqueue <vc ID> <playersEachTeam> <pickingMode (AUTOMATIC/CAPTAINS)> <casual (true/false)>", new String[]{"addq"}, "Add a ranked/casual queue", CommandSubsystem.QUEUE));
        commands.add(new DeleteQueueCmd("deletequeue", "deletequeue <vc ID>", new String[]{"delq", "delqueue"}, "Delete a ranked/casual queue", CommandSubsystem.QUEUE));
        commands.add(new QueuesCmd("queues", "queues", new String[]{}, "View info about all server queues and some info about them", CommandSubsystem.QUEUE));

        commands.add(new AddRankCmd("addrank", "addrank <role ID/mention> <starting elo> <ending elo> <win elo> <lose elo> <mvp elo>", new String[]{"addr"}, "Add a rank", CommandSubsystem.RANK));
        commands.add(new DeleteRankCmd("deleterank", "deleterank <role ID/mention>", new String[]{"delr", "delrank"}, "Delete a rank", CommandSubsystem.RANK));
        commands.add(new RanksCmd("ranks", "ranks", new String[]{}, "View all ranks and info about them", CommandSubsystem.RANK));

        commands.add(new AddMapCmd("addmap", "addmap <name> <height> <team1> <team2>", new String[]{"addm"}, "Add an in-game map", CommandSubsystem.MAP));
        commands.add(new DeleteMapCmd("deletemap", "deletemap <name>", new String[]{"delm", "delmap"}, "Delete an in-game map", CommandSubsystem.MAP));
        commands.add(new MapsCmd("maps", "maps", new String[]{}, "View all maps and info about them", CommandSubsystem.MAP));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = Config.getValue("prefix");

        String[] args = event.getMessage().getContentRaw().split(" ");
        Guild g = event.getGuild();
        Member m = event.getMember();
        TextChannel c = event.getChannel();
        Message msg = event.getMessage();

        if (!msg.getContentRaw().startsWith(prefix)) {
            return;
        }

        if (!Player.isRegistered(m.getId())) {
            Embed reply = new Embed(EmbedType.ERROR, "Not Registered", Msg.getMsg("not-registered"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        if (Boolean.parseBoolean(Config.getValue("log-commands"))) {
            System.out.println("[RankedBot] " + m.getUser() .getAsTag() + " used " + msg.getContentRaw());
        }

        Command command = null;

        for (Command cmd : commands) {
            String[] aliases = cmd.getAliases();
            boolean isAlias = Arrays.asList(aliases).contains(args[0].toLowerCase().replace(prefix, ""));
            if (args[0].replace(prefix, "").equalsIgnoreCase(cmd.getCommand()) || isAlias) {
                command = cmd;
            }
        }

        if (!checkPerms(command, m, g)) {
            Embed reply = new Embed(EmbedType.ERROR, "No Permission", Msg.getMsg("no-perms"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        command.execute(args, g, m, c, msg);
    }

    public static ArrayList<Command> getAllCommands() {
        return commands;
    }

    private boolean checkPerms (Command cmd, Member m, Guild g) {
        boolean access = false;

        if (Perms.getPerm(cmd.getCommand()).equals("everyone")) {
            access = true;
        }
        else {
            if (!Perms.getPerm(cmd.getCommand()).equals("")) {
                List<Role> roles = new ArrayList<>();
                for (String s : Perms.getPerm(cmd.getCommand()).split(",")) {
                    roles.add(g.getRoleById(Long.parseLong(s)));
                }

                for (Role r : m.getRoles()) {
                    if (roles.contains(r)) {
                        access = true;
                        break;
                    }
                }
            }
        }

        return access;
    }
}
