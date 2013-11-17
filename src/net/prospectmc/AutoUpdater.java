package net.prospectmc;

import java.util.HashMap;
import net.prospectmc.QueryBuilder.Clause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for Events which should automatically be updated in the Database
 *
 * @author Cody
 */
public class AutoUpdater implements Listener {
    private static final int MILLIS_PER_MINUTE = 1000 * 60;
    private static HashMap<String, Long> loginTime = new HashMap<String, Long>();

    /**
     * Creates new Users as they log in
     *
     * @param event the PlayerJoinEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            QueryBuilder.insert("Users", "username", event.getPlayer().getName()).async();
        }
        playerLoggedOn(event.getPlayer().getName());
    }

    /**
     * Stores (locally) the time that a player logged on
     *
     * @param playerName The name of the Player
     */
    public static void playerLoggedOn(String playerName) {
        loginTime.put(playerName, System.currentTimeMillis());
    }

    /**
     * Updates a Player's total play time
     *
     * @param event the PlayerQuitEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLoggedOff(event.getPlayer().getName());
    }

    /**
     * Updates a Player's total play time
     *
     * @param playerName The name of the Player
     * @param async true if the query may be executed asynchronously
     */
    public static void playerLoggedOff(String playerName) {
        int minutes = (int) ((System.currentTimeMillis() - loginTime.remove(playerName)) / MILLIS_PER_MINUTE);
        if (minutes >= 0) {
            QueryBuilder.increment("Players", "play_time", minutes, Clause.where("username", playerName)).executeUpdate();
        }
    }

    /**
     * Logs deaths as they occur
     *
     * @param event the PlayerDeathEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
//        Player player = event.getEntity();
//        String killer = "banana banana banana";
//        if (killer == null) {
//            QueryBuilder.insert(
//                    "Log_Deaths",
//                    new String[] {"victim", "zone"},
//                    new String[] {player.getName(), RegionAPI.getZone(player)}
//            ).async();
//        } else {
//            QueryBuilder.insert(
//                    "Log_Deaths",
//                    new String[] {"victim", "killer", "zone"},
//                    new String[] {player.getName(), killer, RegionAPI.getZone(player)}
//            ).async();
//        }
    }

//    Move to ExperienceAPI
//    /**
//     * Logs deaths as they occur
//     *
//     * @param event the PlayerDeathEvent that occurred
//     */
//    @EventHandler (priority = EventPriority.MONITOR)
//    public void onPlayerGainExp(PlayerExpChangeEvent event) {
//        Player player = event.getPlayer();
//        int amount = event.getAmount();
//        if (QueryBuilder.increment("Levels", "total_exp", amount, "username", player.getName()).getSuccess()) {
//            try {
//                int newLevel = QueryBuilder.select("Levels", "level", "username", player.getName()).getInt();
//                while (player.getLevel() < newLevel) {
//                    gainLevel(player);
//                }
//            } catch (SQLException ex) {
//                DatabaseAPI.logger.log(Level.SEVERE, "Query produced invalid results", ex);
//            }
//        }
//    }
}
