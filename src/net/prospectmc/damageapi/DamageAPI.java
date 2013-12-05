package net.prospectmc.damageapi;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Codisimus
 */
public class DamageAPI extends JavaPlugin implements Listener {
    private static HashMap<UUID, DamageSnapshot> projectiles = new HashMap<UUID, DamageSnapshot>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public static void registerProjectile(LivingEntity shooter, org.bukkit.entity.Entity thrown) {
        projectiles.put(thrown.getUniqueId(), new DamageSnapshot(shooter));
    }

    public static DamageSnapshot getDamageSnapshot(Entity damager) {
        return damager instanceof Player
               ? new DamageSnapshot((Player) damager)
               : projectiles.remove(damager.getUniqueId());
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        LivingEntity shooter = projectile.getShooter();
        DamageAPI.registerProjectile(shooter, projectile);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = (LivingEntity) event.getEntity();
        Entity attacker = event instanceof EntityDamageByEntityEvent
                          ? ((EntityDamageByEntityEvent) event).getDamager()
                          : null;
        double damage = event.getDamage();
        DamageCause cause = event.getCause();

        ProspectDamageEvent pEvent = new ProspectDamageEvent(victim, attacker, damage, cause);
        if (pEvent.isCancelled()) {
            event.setCancelled(true);
        } else {
            event.setDamage(pEvent.getDamage());
            if (pEvent.hasSnapshot()) {
                pEvent.getDamageSnapshot().applyDamage(victim);
            }
        }
    }
}
