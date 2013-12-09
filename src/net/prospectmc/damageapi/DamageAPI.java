package net.prospectmc.damageapi;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * DamageAPI is used to apply damage and status effects for weapons/items
 *
 * @author Codisimus
 */
public class DamageAPI extends JavaPlugin implements Listener {
    private static JavaPlugin plugin;
    private static HashMap<UUID, DamageSnapshot> projectiles = new HashMap<UUID, DamageSnapshot>();
    private static HashMap<String, LivingEntity> attributeEffects = new HashMap<String, LivingEntity>();
    private static HashMap<String, BukkitTask> tasks = new HashMap<String, BukkitTask>();

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    /**
     * Registers an entity as being damaged by status effects
     *
     * @param victim The LivingEntity being damaged
     * @param cause The DamageCause that the ProspectDamageEvent will detect
     * @param attacker The LivingEntity which caused the damage
     * @param duration How long the damage is to be registered for
     */
    public static void registerDamageCause(LivingEntity victim, DamageCause cause, LivingEntity attacker, int duration) {
        //Create a unique key
        final String key = victim.getUniqueId() + "." + cause.name();
        //New status effects overwrite old ones
        if (tasks.containsKey(key)) {
            tasks.get(key).cancel();
        }
        attributeEffects.put(key, attacker);
        //Trigger to remove the key in the set duration
        tasks.put(key, new BukkitRunnable() {
            @Override
            public void run() {
                attributeEffects.remove(key);
                tasks.remove(key);
            }
        }.runTaskLater(plugin, duration));
    }

    /**
     * Returns the Entity which is causing the damage cause if any
     *
     * @param victim The LivingEntity being damaged
     * @param cause The DamageCause
     * @return The LivingEntity which is register as the damage causer, or null if none are registered
     */
    public static LivingEntity getAttacker(LivingEntity victim, DamageCause cause) {
        String key = victim.getUniqueId() + "." + cause.name();
        return attributeEffects.get(key);
    }

    /**
     * Registers the given Entity as a projectile
     *
     * @param shooter The LivingEntity responsible for launching the projectile
     * @param thrown The Entity which is being used as a projectile
     */
    public static void registerProjectile(LivingEntity shooter, org.bukkit.entity.Entity thrown) {
        projectiles.put(thrown.getUniqueId(), new DamageSnapshot(shooter));
    }

    /**
     * Returns the DamageSnapshot for the given Entity
     * If the Entity is a Player, a new snapshot will be created
     * otherwise the registered snapshot will be returned if there is one
     *
     * @param damager The Entity which is causing damage
     * @return The DamageSnapshot for the Entity
     */
    public static DamageSnapshot getDamageSnapshot(Entity damager) {
        return damager instanceof Player
               ? new DamageSnapshot((Player) damager)
               : projectiles.remove(damager.getUniqueId());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        LivingEntity shooter = projectile.getShooter();
        if (shooter == null) {
            return;
        }
        DamageAPI.registerProjectile(shooter, projectile);
        //Free up space after time
        new BukkitRunnable() {
            @Override
            public void run() {
                projectiles.remove(projectile.getUniqueId());
            }
        }.runTaskLater(this, 20 * 30);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        //We only care about hurting living things
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity victim = (LivingEntity) event.getEntity();
        Entity attacker = event instanceof EntityDamageByEntityEvent
                          ? ((EntityDamageByEntityEvent) event).getDamager()
                          : null;
        double damage = event.getDamage();
        DamageCause cause = event.getCause();

        //Trigger a ProspectDamageEvent
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
