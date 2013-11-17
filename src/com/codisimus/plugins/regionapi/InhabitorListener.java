package com.codisimus.plugins.regionapi;

import com.codisimus.plugins.regiontools.Region;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Regenerate Health/Hunger & Alarm System & Notify When in Regions
 *
 * @author Codisimus
 */
public class InhabitorListener implements Listener {
    static HashMap<Player, Region> inRegion = new HashMap<Player, Region>();
    static HashMap<Region, HashSet<Player>> regionInhabitors = new HashMap<Region, HashSet<Player>>();

    @EventHandler (ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        //Return if the Player did not move between Blocks
        Block to = event.getTo().getBlock();
        Location location = event.getFrom();
        Block from = location.getBlock();
        if (to.equals(from)) {
            return;
        }

        Player player = event.getPlayer();

        Region regionEntered = RegionAPI.findRegion(to.getLocation(), false);
        Region regionLeft = inRegion.get(player);
        if (regionEntered == regionLeft) {          //Region1 -> Region1
            return;
        }

        PlayerChangeRegionEvent regionEvent = new PlayerChangeRegionEvent(player, regionLeft, regionEntered);

        if (regionEntered == null) {                //? -> Wilderness
            if (regionLeft != null) {               //Region1 -> Wilderness
                playerLeftRegion(player, regionLeft);
            }                                       //Wilderness -> Wilderness
        } else {
            if (regionLeft != null) {               //Wilderness -> Region2
                playerLeftRegion(player, regionLeft);
            }                                       //Region1 -> Region2
            playerEnteredRegion(player, regionEntered);
        }
    }

    private static void playerLeftRegion(Player player, Region region) {
        regionInhabitors.get(region).remove(player);
        inRegion.remove(player);
        //player.sendMessage(RegionAPI.getMessage().replace("<name>", region.getName()));
    }

    private static void playerEnteredRegion(Player player, Region region) {
        if (!regionInhabitors.containsKey(region)) {
            regionInhabitors.put(region, new HashSet<Player>());
        }
        regionInhabitors.get(region).add(player);
        inRegion.put(player, region);
        player.sendMessage("Now entering " + region.getName());
    }

    public static HashSet<Player> getPlayersInRegion(Region region) {
        return regionInhabitors.containsKey(region) ? regionInhabitors.get(region) : new HashSet<Player>();
    }
}