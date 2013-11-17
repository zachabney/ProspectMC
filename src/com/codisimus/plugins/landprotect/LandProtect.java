package com.codisimus.plugins.landprotect;

import com.codisimus.plugins.regiontools.Region;
import com.codisimus.plugins.regiontools.RegionTools;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LandProtect extends JavaPlugin implements Listener {
    private static final String BUILD_PERMISSION = "build";
    private static final String BUILD_RESTRICTED_PERMISSION = "build.restrict";
    private static final String BUILD_RESTRICTIONS = "Build_Restrictions";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setBuild(canBuild(event.getPlayer()));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!canBuild(event.getPlayer()));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        event.setCancelled(event.getPlayer() == null || !canBuild(event.getPlayer()));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBlockExp(BlockExpEvent event) {
        event.setExpToDrop(0);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreakDoor(EntityBreakDoorEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onEntityTame(EntityTameEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLeashEntity(PlayerLeashEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        event.setCancelled(!(event.getRemover() instanceof Player)
                || canBuild((Player) event.getRemover()));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBucket(PlayerBucketEmptyEvent event) {
        event.setCancelled(!canBuild(event.getPlayer()));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBucket(PlayerBucketFillEvent event) {
        event.setCancelled(!canBuild(event.getPlayer()));
    }

    public boolean canBuild(Player player) {
        if (!player.hasPermission(BUILD_PERMISSION)) {
            return false;
        } else if (player.hasPermission(BUILD_RESTRICTED_PERMISSION)) {
            Region region = RegionTools.findRegion(BUILD_RESTRICTIONS, false).getChild(player.getName());
            if (region != null) {
                return region.contains(player.getLocation());
            }
        }
        return true;
    }
}
