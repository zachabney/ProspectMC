package net.prospectmc;

import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A Faction is a group of Players
 *
 * @author Cody
 */
public enum Faction {
    EMPIRE,
    SETTLERS,
    UNDECIDED;

    private HashSet<String> members = new HashSet<String>();

    public HashSet getMembers() {
        return members;
    }

    public boolean hasMember(String playerName) {
        return members.contains(playerName);
    }

    void forgetMember(final String playerName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPlayerExact(playerName) == null) {
                    members.remove(playerName);
                }
            }
        }.runTaskLater(Factions.plugin, 1000 * 60 * 10);
    }

    void reMember(String playerName) {
        members.add(playerName);
    }

    static boolean hasMemberRecord(String playerName) {
        for (Faction faction : values()) {
            if (faction.hasMember(playerName)) {
                return true;
            }
        }
        return false;
    }
}
