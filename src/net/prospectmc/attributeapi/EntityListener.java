package net.prospectmc.attributeapi;

import net.minecraft.server.v1_7_R1.AttributeMapServer;
import net.minecraft.server.v1_7_R1.EntityLiving;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
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
    public void onPlayerJoin(final PlayerJoinEvent event) {
        registerAttributes(event.getPlayer());
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        registerAttributes(event.getEntity());
    }

    /**
     * Convenience method which converts LivingEntity to EntityLiving
     *
     * @param entity The given LivingEntity
     */
    public static void registerAttributes(LivingEntity entity) {
        registerAttributes(((CraftLivingEntity) entity).getHandle());
    }

    /**
     * Registers custom Attributes with the given EntityLiving
     *
     * @param entity The given EntityLiving
     */
    private static void registerAttributes(EntityLiving entity) {
        AttributeMapServer attributemapserver = (AttributeMapServer) entity.bc();
        for (Attribute attribute : AttributeAPI.getAttributes()) {
            if (attribute.isCustom()) {
                attributemapserver.b(attribute.getIAttribute());
            }
        }
    }
}
