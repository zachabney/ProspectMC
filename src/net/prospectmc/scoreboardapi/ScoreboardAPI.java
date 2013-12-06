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

public class ScoreboardAPI extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		createScoreboard(event.getPlayer());
	}

	private static void createScoreboard(Player player) {
		System.out.println("Create");
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective factionObj = board.getObjective("faction-level");
		if(factionObj == null) {
			factionObj = board.registerNewObjective("faction-level", "dummy");
			factionObj.setDisplayName("§4" + player.getName());
			factionObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
		}
		factionObj.getScore(player).setScore(5);
		for(Player p : Bukkit.getOnlinePlayers()) p.setScoreboard(board);
	}

}
