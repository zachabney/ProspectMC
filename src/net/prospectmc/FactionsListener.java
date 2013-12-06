package net.prospectmc;

import java.util.logging.Level;
import net.prospectmc.QueryBuilder.Clause;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for Events which are affected by a Player's faction
 *
 * @author Cody
 */
public class FactionsListener implements Listener {

    /**
     * Stores (locally) the faction of a Player
     *
     * @param event the PlayerJoinEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerLoggedOn(event.getPlayer().getName());
    }

    /**
     * Stores (locally) the faction of a Player
     *
     * @param playerName The name of the Player
     */
    public static void playerLoggedOn(String playerName) {
        if (!Faction.hasMemberRecord(playerName)) {
            Faction faction;
            try {
                String factionName = QueryBuilder.select("Players", "faction", Clause.where("username", playerName)).getString();
                faction = Faction.valueOf(factionName.toUpperCase());
            } catch (Exception ex) {
                Factions.logger.log(Level.SEVERE, "Player record not found!", ex);
                faction = Faction.UNDECIDED;
            }
            faction.reMember(playerName);
        }
    }

    /**
     * Schedules to clear up memory by dropping offline players
     *
     * @param event the PlayerQuitEvent that occurred
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLoggedOff(event.getPlayer().getName());
    }

    /**
     * Removes a Player's faction status from memory
     *
     * @param playerName The name of the Player
     */
    public static void playerLoggedOff(String playerName) {
        FactionAPI.findFaction(playerName).forgetMember(playerName);
    }

//    @EventHandler (priority = EventPriority.LOWEST)
//    public void onPvP(EntityDamageByEntityEvent event) {
//        if (!event.getEntity() instanceof Player) {
//
//        }
//        event.getCause().
//    }
}
