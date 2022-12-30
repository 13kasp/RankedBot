package com.kasp.rankedbot.instance;

import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.cache.PartyCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Party {

    private Player leader;
    private List<Player> members;
    private List<Player> invitedPlayers;

    public Party(Player leader) {
        this.leader = leader;
        members = new ArrayList<>();
        invitedPlayers = new ArrayList<>();
        members.add(leader);

        PartyCache.initializeParty(this);
    }

    public void invite(Player invited) {

        invitedPlayers.add(invited);

        TimerTask inviteExpiration = new TimerTask() {
            @Override
            public void run() {
                if (invitedPlayers.contains(invited)) {
                    invitedPlayers.remove(invited);
                }
            }
        };
        new Timer().schedule(inviteExpiration, Integer.parseInt(Config.getValue("invite-expiration")) * 60 * 1000L);
    }

    public void disband() {
        PartyCache.removeParty(this);
    }

    public void promote(Player player) {
        leader = player;
    }

    public Player getLeader() {
        return leader;
    }
    public void setLeader(Player leader) {
        this.leader = leader;
    }
    public List<Player> getMembers() {
        return members;
    }
    public void setMembers(List<Player> members) {
        this.members = members;
    }
    public List<Player> getInvitedPlayers() {
        return invitedPlayers;
    }
    public void setInvitedPlayers(List<Player> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }
}
