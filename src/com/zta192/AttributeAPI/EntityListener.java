package com.zta192.AttributeAPI;

import net.minecraft.server.v1_7_R1.AttributeMapServer;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.IAttribute;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Applies Attributes to all Entities as they come into existence
 *
 * @author Codisimus
 */
public class EntityListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        registerAttributes(event.getPlayer());
    }

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
