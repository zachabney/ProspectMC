package net.prospectmc.scoreboardapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardAPI extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team1 = board.getTeam("team1");
		if(team1 == null) team1 = board.registerNewTeam("team1");
		Team team2 = board.getTeam("team2");
		if(team2 == null) team2 = board.registerNewTeam("team2");
		team1.setPrefix("§a");
		team2.setPrefix("§c");

		if(team1.getSize() > 0) {
			team2.addPlayer(event.getPlayer());
		} else {
                        team1.addPlayer(event.getPlayer());
                }
                event.getPlayer().sendMessage("You have been added to team " + (team1.getPlayers().contains(event.getPlayer()) ? 1 : 2));
		event.getPlayer().setScoreboard(board);
	}

}
