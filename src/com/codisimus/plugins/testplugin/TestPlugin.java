package com.codisimus.plugins.testplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        new CommandHandler(this).registerCommands(BlindCommand.class);
        new CommandHandler(this, "test").registerCommands(TestCommand.class);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

//    @EventHandler
//    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
//        System.out.println("---------------------------------------");
//        System.out.println("Entity: " + event.getEntityType().getName());
//        System.out.println("Target: " + event.getTarget().getType().getName());
//        System.out.println("Reason: " + event.getReason().name());
//        System.out.println("---------------------------------------");
//    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
}
