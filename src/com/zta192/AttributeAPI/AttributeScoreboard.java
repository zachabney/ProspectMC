package com.zta192.AttributeAPI;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class AttributeScoreboard {
    private static final HashMap<String, Scoreboard> scoreboards = new HashMap<String, Scoreboard>();
    public static void show(Player player) {
        Scoreboard scoreboard;
        if (scoreboards.containsKey(player.getName())) {
            scoreboard = scoreboards.get(player.getName());
        } else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("attributes", "dummy");
            objective.setDisplayName("Attributes");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        player.setScoreboard(scoreboard);
        scoreboards.put(player.getName(), scoreboard);
        update(player);
    }

    public static void update(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("attributes");
        for (Attribute attribute : Attribute.values()) {
            Score score = objective.getScore(Bukkit.getOfflinePlayer(attribute.getName()));
            score.setScore((int) AttributeAPI.getValue(player, attribute));
        }
    }
}
