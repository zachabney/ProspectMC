package net.prospectmc.scoreboardapi;

import net.prospectmc.Faction;
import net.prospectmc.FactionAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardAPI extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(board);

                for (Faction faction : Faction.values()) {
                        Team t = board.registerNewTeam(faction.toString());
                        t.setPrefix(faction.getColor().toString());
                        for (String playerName : faction.getMembers()) {
                                t.addPlayer(Bukkit.getOfflinePlayer(playerName));
                        }
                }

		Team team = board.getTeam(FactionAPI.findFaction(player.getName()).toString());
                for (Player p : Bukkit.getOnlinePlayers()) {
                        p.getScoreboard().getTeam(team.getName()).addPlayer(player);
                }
	}
}
