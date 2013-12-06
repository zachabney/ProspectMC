package com.zta192.AttributeAPI;

import net.minecraft.server.v1_7_R1.AttributeMapServer;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.IAttribute;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Applies Attributes to all Entities as they come into existence
 *
 * @author Codisimus
 */
public class EntityListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        registerAttributes(event.getPlayer());
        //AttributeScoreboard.show(event.getPlayer());
    }

//    @EventHandler (priority = EventPriority.MONITOR)
//    public void onItemHeld(PlayerItemHeldEvent event) {
//        AttributeScoreboard.update((Player) event.getPlayer());
//    }
//
//    @EventHandler
//    public void onInventoryClose(InventoryCloseEvent event) {
//        if (event.getPlayer() instanceof Player) {
//            AttributeScoreboard.update((Player) event.getPlayer());
//        }
//    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        registerAttributes(event.getEntity());
    }

    public static void registerAttributes(LivingEntity entity) {
        registerAttributes(((CraftLivingEntity) entity).getHandle());
    }

    private static void registerAttributes(EntityLiving entity) {
        AttributeMapServer attributemapserver = (AttributeMapServer) entity.bc();
        for (IAttribute attribute : AttributeAPI.getAttributes()) {
            attributemapserver.b(attribute);
        }
    }
}
